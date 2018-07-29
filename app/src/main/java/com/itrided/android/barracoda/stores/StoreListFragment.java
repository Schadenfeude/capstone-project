package com.itrided.android.barracoda.stores;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itrided.android.barracoda.BarraCodaApp;
import com.itrided.android.barracoda.R;
import com.itrided.android.barracoda.data.model.db.Store;
import com.itrided.android.barracoda.databinding.FragmentStoreListBinding;
import com.itrided.android.barracoda.permissions.PermissionManager;
import com.itrided.android.barracoda.stores.location.StoreFinderController;

import io.reactivex.Completable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;
import static android.content.pm.PackageManager.PERMISSION_DENIED;
import static com.itrided.android.barracoda.permissions.PermissionManager.RC_HANDLE_PERM;
import static com.itrided.android.barracoda.stores.location.StoreFinderController.RC_PLACE_PICKER;

public class StoreListFragment extends Fragment {

    public static final String TAG = StoreListFragment.class.getSimpleName();

    private FragmentStoreListBinding binding;
    private StoreFinderController storeController;
    private StoreListAdapter storeListAdapter;

    public StoreListFragment() {
    }

    private static StoreListFragment INSTANCE = null;

    public static StoreListFragment getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new StoreListFragment();
        }
        return INSTANCE;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storeListAdapter = new StoreListAdapter();
        storeController = new StoreFinderController(this, storeListAdapter);
        getLifecycle().addObserver(storeController);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentStoreListBinding.inflate(inflater, container, false);

        binding.storesRv.setAdapter(storeListAdapter);
        binding.storesRv.addItemDecoration(new DividerItemDecoration(binding.storesRv.getContext(),
                LinearLayoutManager.VERTICAL));
        binding.fabAddStore.setOnClickListener(getAddStoreClickListener());

        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_PLACE_PICKER && resultCode == RESULT_OK) {
            handleStoreResult(data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == RC_HANDLE_PERM && grantResults[0] == PERMISSION_DENIED) {
            final Snackbar snackbar = Snackbar
                    .make(binding.getRoot(), R.string.no_location_permission_message, Snackbar.LENGTH_INDEFINITE)
                    .setAction("Give Permission", view -> PermissionManager.requestPermission(
                            this, PermissionManager.Permission.FINE_LOCATION));
            snackbar.show();
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private View.OnClickListener getAddStoreClickListener() {
        return v -> storeController.addPlace(v);
    }

    private void handleStoreResult(Intent data) {
        final Place place = PlacePicker.getPlace(this.getContext(), data);
        if (place == null) {
            Log.i(TAG, "No place selected");
            return;
        }

        final String placeID = place.getId();

        addStore(placeID);
    }

    private void addStore(@NonNull String storeId) {
        Completable.create(
                emitter -> {
                    BarraCodaApp.getDatabaseInstance().storeModel().addStore(new Store(storeId));
                    emitter.onComplete();
                })
                .subscribeOn(Schedulers.io())
                .subscribe();
    }
}
