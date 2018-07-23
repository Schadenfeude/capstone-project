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

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.itrided.android.barcodescanner.ui.barcode.BarcodeGraphic;
import com.itrided.android.barcodescanner.ui.barcode.BarcodeGraphicTracker;
import com.itrided.android.barcodescanner.ui.barcode.BarcodeTrackerFactory;
import com.itrided.android.barcodescanner.ui.camera.CameraSourcePreview;
import com.itrided.android.barcodescanner.ui.camera.GraphicOverlay;
import com.itrided.android.barracoda.BarcodeScanActivity;
import com.itrided.android.barracoda.BarraCodaApp;
import com.itrided.android.barracoda.R;
import com.itrided.android.barracoda.data.model.api.ApiProduct;
import com.itrided.android.barracoda.databinding.ContentScanBinding;
import com.itrided.android.barracoda.product.ProductDetailFragment;
import com.itrided.android.barracoda.product.ProductViewModel;

import java.io.IOException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;

/**
 * Activity for the multi-tracker app.  This app detects barcodes and displays the value with the
 * rear facing camera. During detection overlay graphics are drawn to indicate the position,
 * size, and ID of each barcode.
 */
public final class BarcodeCaptureController implements LifecycleObserver {
    private static final String TAG = BarcodeCaptureController.class.getSimpleName();

    // intent request code to handle updating play services if needed.
    private static final int RC_HANDLE_GMS = 9001;

    // permission request codes need to be < 256
    public static final int RC_HANDLE_CAMERA_PERM = 2;

    // constants used to pass extra data in the intent
    public static final String AutoFocus = "AutoFocus";
    public static final String UseFlash = "UseFlash";
    public static final String BarcodeObject = "Barcode";

    private static final String FRAGMENT_DETAIL_TAG = "FRAGMENT_DETAIL_TAG";

    private Activity activity;
    private ContentScanBinding contentScanBinding;
    private ProductViewModel productViewModel;

    private CameraSource mCameraSource;
    private CameraSourcePreview mPreview;
    private GraphicOverlay<BarcodeGraphic> mGraphicOverlay;
    private BarcodeGraphicTracker.BarcodeUpdateListener barcodeUpdateListener;

    // helper objects for detecting taps
    private GestureDetector gestureDetector;


    /**
     * Initializes the UI and creates the detector pipeline.
     */
    public BarcodeCaptureController(final Activity activity, final ContentScanBinding contentScanBinding) {
        this.activity = activity;
        this.contentScanBinding = contentScanBinding;
        this.productViewModel = ViewModelProviders.of((FragmentActivity) activity).get(ProductViewModel.class);

        mPreview = contentScanBinding.preview;
        mGraphicOverlay = contentScanBinding.graphicOverlay;

//         Check for the camera permission before accessing the camera.  If the
//         permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            boolean autoFocus = true;
            boolean useFlash = false;
            createCameraSource(autoFocus, useFlash);
        } else {
            requestCameraPermission();
        }

        gestureDetector = new GestureDetector(activity, new CaptureGestureListener());

        Snackbar.make(mGraphicOverlay, "Tap to capture. Pinch/Stretch to zoom",
                Snackbar.LENGTH_LONG)
                .show();
    }

    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(activity, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final View.OnClickListener listener = view -> ActivityCompat.requestPermissions(activity,
                permissions, RC_HANDLE_CAMERA_PERM);

        contentScanBinding.topView.setOnClickListener(listener);
        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    private BarcodeGraphicTracker.BarcodeUpdateListener getBarcodeUpdateListener() {
        return barcode -> {
            Log.d(TAG, "getBarcodeUpdateListener: " + barcode.rawValue);
            BarraCodaApp.getRecipeService().getProduct(barcode.rawValue)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<ApiProduct>() {
                        @Override
                        public void onSuccess(@Nullable ApiProduct result) {
                            if (result == null) {
                                return;
                            }
                            productViewModel.setProduct(result);
                            launchDetailFragment();
                        }

                        @Override
                        public void onError(Throwable e) {
                            //todo handle error
                            e.printStackTrace();
                        }
                    });
        };
    }

    private void launchDetailFragment() {
        final FragmentManager fragmentManager = ((FragmentActivity) activity).getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        final ProductDetailFragment productDetailFragment = ProductDetailFragment.getInstance();

        transaction.replace(R.id.content_scan, productDetailFragment, FRAGMENT_DETAIL_TAG);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public boolean onTouchEvent(MotionEvent e) {
        boolean c = gestureDetector.onTouchEvent(e);

        return c || activity.onTouchEvent(e);
    }

    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the barcode detector to detect small barcodes
     * at long distances.
     *
     * Suppressing InlinedApi since there is a check that the minimum version is met before using
     * the constant.
     */
    private void createCameraSource(boolean autoFocus, boolean useFlash) {
        final Context context = activity.getApplicationContext();

        // A barcode detector is created to track barcodes.  An associated multi-processor instance
        // is set to receive the barcode detection results, track the barcodes, and maintain
        // graphics for each barcode on screen.  The factory is used by the multi-processor to
        // create a separate tracker instance for each barcode.
        final BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(context).build();
        final BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory(mGraphicOverlay, getBarcodeUpdateListener());
        barcodeDetector.setProcessor(
                new MultiProcessor.Builder<>(barcodeFactory).build());

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
            final boolean hasLowStorage = activity.registerReceiver(null, lowStorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(activity, R.string.low_storage_error, Toast.LENGTH_LONG).show();
                Log.w(TAG, activity.getString(R.string.low_storage_error));
            }
        }

        // Creates and starts the camera.  Note that this uses a higher resolution in comparison
        // to other detection examples to enable the barcode detector to detect small barcodes
        // at long distances.
        mCameraSource = new CameraSource.Builder(activity.getApplicationContext(), barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(1600, 1024)
                .setRequestedFps(15.0f)
                .build();
    }

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
        if (mPreview != null) {
            mPreview.stop();
        }
    }

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    protected void onDestroy() {
        if (mPreview != null) {
            mPreview.release();
        }
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on
     * {@link BarcodeScanActivity#requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see BarcodeScanActivity#requestPermissions(String[], int)
     */
    public void onRequestPermissionsResult(@NonNull int[] grantResults) {

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
            boolean autoFocus = true;/*getIntent().getBooleanExtra(AutoFocus, false);*/
            boolean useFlash = true;/*getIntent().getBooleanExtra(UseFlash, false);*/
            createCameraSource(autoFocus, useFlash);
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = (dialog, id) -> {
//                finish();
        };

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Multitracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
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
            final Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(activity, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
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
        mGraphicOverlay.getLocationOnScreen(location);
        final float x = (rawX - location[0]) / mGraphicOverlay.getWidthScaleFactor();
        final float y = (rawY - location[1]) / mGraphicOverlay.getHeightScaleFactor();

        // Find the barcode whose center is closest to the tapped point.
        Barcode best = null;
        float bestDistance = Float.MAX_VALUE;
        for (BarcodeGraphic graphic : mGraphicOverlay.getGraphics()) {
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
}
