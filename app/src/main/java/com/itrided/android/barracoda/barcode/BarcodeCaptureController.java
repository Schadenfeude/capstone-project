/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.itrided.android.barracoda.barcode;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import android.app.Activity;
import android.app.AlertDialog;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

import com.itrided.android.barcodescanner.ui.barcode.BarcodeGraphic;
import com.itrided.android.barcodescanner.ui.barcode.BarcodeGraphicTracker;
import com.itrided.android.barcodescanner.ui.barcode.BarcodeTrackerFactory;
import com.itrided.android.barcodescanner.ui.camera.CameraSourcePreview;
import com.itrided.android.barcodescanner.ui.camera.GraphicOverlay;
import com.itrided.android.barracoda.R;
import com.itrided.android.barracoda.permissions.PermissionManager;

import java.io.IOException;

import static com.itrided.android.barracoda.permissions.PermissionManager.Permission.CAMERA;

/**
 * Activity for the multi-tracker app.  This app detects barcodes and displays the value with the
 * rear facing camera. During detection overlay graphics are drawn to indicate the position,
 * size, and ID of each barcode.
 */
public final class BarcodeCaptureController implements LifecycleObserver,
        ActivityCompat.OnRequestPermissionsResultCallback {
    private static final String TAG = BarcodeCaptureController.class.getSimpleName();

    // intent request code to handle updating play services if needed.
    private static final int RC_HANDLE_GMS = 9001;

    public static final String BarcodeObject = "Barcode";

    private Activity activity;
    private CameraSource cameraSource;
    private CameraSourcePreview preview;
    private GraphicOverlay<BarcodeGraphic> graphicOverlay;
    // helper objects for detecting taps
    private GestureDetector gestureDetector;

    private BarcodeGraphicTracker.BarcodeUpdateListener barcodeUpdateListener;

    /**
     * Initializes the UI and creates the detector pipeline.
     */
    public <P extends CameraSourcePreview, O extends GraphicOverlay<BarcodeGraphic>>
    BarcodeCaptureController(final Activity context,
                             final P preview,
                             final O overlay) {
        this.activity = context;
        this.preview = preview;
        this.graphicOverlay = overlay;

        /*  Check for the camera permission before accessing the camera.
            If the permission is not granted yet, request permission    */
        if (PermissionManager.checkPermission(context, CAMERA)) {
            createCameraSource();
            this.gestureDetector = new GestureDetector(context, new CaptureGestureListener());
            Snackbar.make(graphicOverlay, R.string.barcode_capture_tooltip, Snackbar.LENGTH_LONG).show();
        } else {
            PermissionManager.requestPermission(context, CAMERA);
        }
    }

//    private BarcodeGraphicTracker.BarcodeUpdateListener getBarcodeUpdateListener() {
//        return barcode -> {
//            Log.d(TAG, "getBarcodeUpdateListener: " + barcode.rawValue);
//            BarraCodaApp.getRecipeService().getProduct(barcode.rawValue)
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribeWith(new DisposableSingleObserver<ApiProduct>() {
//                        @Override
//                        public void onSuccess(@Nullable ApiProduct result) {
//                            if (result == null) {
//                                return;
//                            }
//                            productViewModel.setProduct(result);
//                            launchDetailFragment();
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//                            //todo handle error
//                            e.printStackTrace();
//                        }
//                    });
//        };
//    }

//    private void launchDetailFragment() {
//        final FragmentManager fragmentManager = ((FragmentActivity) activity).getSupportFragmentManager();
//        final FragmentTransaction transaction = fragmentManager.beginTransaction();
//        final ProductDetailFragment productDetailFragment = ProductDetailFragment.getInstance();
//
//        transaction.replace(R.id.content_scan, productDetailFragment, FRAGMENT_DETAIL_TAG);
//        transaction.addToBackStack(null);
//        transaction.commit();
//    }

    //region API Methods

    /**
     * Restarts the camera.
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    protected void onResume() {
        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    protected void onPause() {
        if (preview != null) {
            preview.stop();
        }
    }

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    protected void onDestroy() {
        if (preview != null) {
            preview.release();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            //todo handle not granted case
            DialogInterface.OnClickListener listener = (dialog, id) -> {
//                finish();
            };

            final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Permission not granted.")
                    .setMessage(R.string.no_camera_permission)
                    .setPositiveButton(R.string.ok, listener)
                    .show();
        }
    }

    public boolean onTouchEvent(@NonNull Activity activity, MotionEvent e) {
        boolean c = gestureDetector.onTouchEvent(e);

        return c || activity.onTouchEvent(e);
    }
    //endregion API Methods

    //region Private Methods

    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the barcode detector to detect small barcodes
     * at long distances.
     *
     * Suppressing InlinedApi since there is a check that the minimum version is met before using
     * the constant.
     */
    private void createCameraSource() {
        final Context context = this.activity.getApplicationContext();

        // A barcode detector is created to track barcodes.  An associated multi-processor instance
        // is set to receive the barcode detection results, track the barcodes, and maintain
        // graphics for each barcode on screen.  The factory is used by the multi-processor to
        // create a separate tracker instance for each barcode.
        final BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(context).build();
        final BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory(graphicOverlay, barcodeUpdateListener);

        barcodeDetector.setProcessor(new MultiProcessor.Builder<>(barcodeFactory).build());
        if (!barcodeDetector.isOperational()) {
            // Note: The first time that an app using the barcode or face API is installed on a
            // device, GMS will download a native libraries to the device in order to do detection.
            // Usually this completes before the app is run for the first time.  But if that
            // download has not yet completed, then the above call will not detect any barcodes
            // and/or faces.
            //
            // isOperational() can be used to check if the required native libraries are currently
            // available.  The detectors will automatically become operational once the library
            // downloads complete on device.
            Log.w(TAG, "Detector dependencies are not yet available.");

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            final IntentFilter lowStorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            final boolean hasLowStorage = this.activity.registerReceiver(null, lowStorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(this.activity, R.string.low_storage_error, Toast.LENGTH_LONG).show();
                Log.w(TAG, this.activity.getString(R.string.low_storage_error));
            }
        }

        // Creates and starts the camera.  Note that this uses a higher resolution in comparison
        // to other detection examples to enable the barcode detector to detect small barcodes
        // at long distances.
        cameraSource = new CameraSource.Builder(activity.getApplicationContext(), barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(1600, 1024)
                .setRequestedFps(15.0f)
                .build();
    }

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() throws SecurityException {
        // check that the device has play services available.
        final int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                activity.getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            GoogleApiAvailability.getInstance().getErrorDialog(activity, code, RC_HANDLE_GMS).show();
        }

        if (cameraSource != null) {
            try {
                preview.start(cameraSource, graphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                cameraSource.release();
                cameraSource = null;
            }
        }
    }

    /**
     * onTap returns the tapped barcode result to the calling Activity.
     *
     * @param rawX - the raw position of the tap
     * @param rawY - the raw position of the tap.
     * @return true if the activity is ending.
     */
    private boolean onTap(float rawX, float rawY) {
        // Find tap point in preview frame coordinates.
        final int[] location = new int[2];
        graphicOverlay.getLocationOnScreen(location);
        final float x = (rawX - location[0]) / graphicOverlay.getWidthScaleFactor();
        final float y = (rawY - location[1]) / graphicOverlay.getHeightScaleFactor();

        // Find the barcode whose center is closest to the tapped point.
        Barcode best = null;
        float bestDistance = Float.MAX_VALUE;
        for (BarcodeGraphic graphic : graphicOverlay.getGraphics()) {
            Barcode barcode = graphic.getBarcode();
            if (barcode.getBoundingBox().contains((int) x, (int) y)) {
                // Exact hit, no need to keep looking.
                best = barcode;
                break;
            }
            float dx = x - barcode.getBoundingBox().centerX();
            float dy = y - barcode.getBoundingBox().centerY();
            float distance = (dx * dx) + (dy * dy);  // actually squared distance
            if (distance < bestDistance) {
                best = barcode;
                bestDistance = distance;
            }
        }

        if (best != null) {
            //todo check if this works and handle if it does
            //todo delete if it doesn't
            Intent data = new Intent();
            data.putExtra(BarcodeObject, best);
//            setResult(CommonStatusCodes.SUCCESS, data);
//            finish();
            return true;
        }
        return false;
    }

    private class CaptureGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return onTap(e.getRawX(), e.getRawY()) || super.onSingleTapConfirmed(e);
        }
    }
    //endregion Private Methods
}
