package com.itrided.android.barracoda.barcode;

import com.google.android.gms.vision.barcode.Barcode;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.itrided.android.barracoda.BarraCodaApp;
import com.itrided.android.barracoda.R;
import com.itrided.android.barracoda.data.model.api.ProductPojo;
import com.itrided.android.barracoda.databinding.FragmentScanBinding;
import com.itrided.android.barracoda.products.product.ProductDetailFragment;
import com.itrided.android.barracoda.products.product.ProductViewModel;
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.PublishSubject;
import okhttp3.ResponseBody;

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

    private CompositeDisposable disposable;
    private PublishSubject<Barcode> barcodes = PublishSubject.create();
    private Observable<ResponseBody> apiProductObservable = barcodes
            .switchMapSingle(barcode -> BarraCodaApp.getBackupBarcodeService().getProduct(barcode.rawValue));

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        productViewModel = ViewModelProviders.of(getActivity()).get(ProductViewModel.class);
    }

    @Nullable
    @Override
    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentScanBinding.inflate(inflater, container, false);
        captureController = new BarcodeCaptureController(this, binding.preview,
                binding.graphicOverlay, barcode -> barcodes.onNext(barcode));

        getLifecycle().addObserver(captureController);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        registerBarcodeResultReceiver();
    }

    @Override
    public void onPause() {
        super.onPause();
        disposable.dispose();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == RC_HANDLE_PERM) {
            captureController.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void launchDetailFragment() {
        final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        final ProductDetailFragment productDetailFragment = new ProductDetailFragment();

        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        transaction.replace(R.id.fragment_placeholder, productDetailFragment, ProductDetailFragment.TAG);
        transaction.addToBackStack(ProductDetailFragment.TAG);
        transaction.commit();
    }

    private void registerBarcodeResultReceiver() {
        disposable = new CompositeDisposable();
        disposable.add(apiProductObservable
                .firstElement()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    final String resultJson = result.string();
                    final ProductPojo product = BarraCodaApp.getBackupGsonService()
                            .fromJson(resultJson, ProductPojo.class);

                    productViewModel.setProduct(product);
                    launchDetailFragment();
                }, e -> {
                    final HttpException exception = (HttpException) e;
                    handleExceptions(exception);
                    registerBarcodeResultReceiver();
                }));
    }

    private void handleExceptions(HttpException error) {
        switch (error.code()) {
            case 404:
                showDialog(R.string.upc_not_found);
                break;
            case 403:
                showDialog(R.string.no_upc_db_key);
                break;
            case 400:
                showDialog(R.string.invalid_upc);
                break;
        }
    }

    private void showDialog(@StringRes int string) {
        Toast.makeText(this.getContext(), string, Toast.LENGTH_LONG).show();
    }
}
