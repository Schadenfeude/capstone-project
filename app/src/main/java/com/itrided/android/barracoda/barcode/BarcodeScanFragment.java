package com.itrided.android.barracoda.barcode;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itrided.android.barcodescanner.ui.barcode.BarcodeGraphicTracker;
import com.itrided.android.barracoda.BarraCodaApp;
import com.itrided.android.barracoda.R;
import com.itrided.android.barracoda.data.model.api.ApiProduct;
import com.itrided.android.barracoda.databinding.FragmentScanBinding;
import com.itrided.android.barracoda.product.ProductDetailFragment;
import com.itrided.android.barracoda.product.ProductViewModel;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;

import static com.itrided.android.barracoda.permissions.PermissionManager.RC_HANDLE_PERM;

public class BarcodeScanFragment extends Fragment {
    public static final String TAG = BarcodeScanFragment.class.getSimpleName();

    private static BarcodeScanFragment INSTANCE = null;

    public static BarcodeScanFragment getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BarcodeScanFragment();
        }
        return INSTANCE;
    }

    private FragmentScanBinding binding;
    private ProductViewModel productViewModel;
    private BarcodeCaptureController captureController;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        productViewModel = ViewModelProviders.of(getActivity()).get(ProductViewModel.class);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentScanBinding.inflate(inflater, container, false);
        captureController = new BarcodeCaptureController(this, binding.preview,
                binding.graphicOverlay, getBarcodeUpdateListener());

        getLifecycle().addObserver(captureController);
        return binding.getRoot();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == RC_HANDLE_PERM) {
            captureController.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
        final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        final ProductDetailFragment productDetailFragment = ProductDetailFragment.getInstance();

        transaction.replace(R.id.fragment_placeholder, productDetailFragment, ProductDetailFragment.TAG);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
