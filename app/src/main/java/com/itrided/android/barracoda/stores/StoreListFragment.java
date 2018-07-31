package com.itrided.android.barracoda.stores;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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
import com.itrided.android.barracoda.ui.DisposableFragment;
import com.itrided.android.barracoda.ui.SwipeToDeleteCallback;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;
import static android.content.pm.PackageManager.PERMISSION_DENIED;
import static com.itrided.android.barracoda.permissions.PermissionManager.RC_HANDLE_PERM;
import static com.itrided.android.barracoda.stores.location.StoreFinderController.RC_PLACE_PICKER;

public class StoreListFragment extends DisposableFragment {

    public static final String TAG = StoreListFragment.class.getSimpleName();

    private FragmentStoreListBinding binding;
    private StoreFinderController storeController;
    private StoreListAdapter storeListAdapter;
    private StoreListViewModel storeListViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        storeListViewModel = ViewModelProviders.of(this).get(StoreListViewModel.class);
        storeListAdapter = new StoreListAdapter();
        storeController = new StoreFinderController(this, storeListAdapter);
        getLifecycle().addObserver(storeController);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentStoreListBinding.inflate(inflater, container, false);
        final ItemTouchHelper ith = new ItemTouchHelper(getSwipeHandler(this.getContext()));

        ith.attachToRecyclerView(binding.storesRv);
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

    private void deleteStore(@NonNull String storeId) {
        Completable.create(
                emitter -> {
                    BarraCodaApp.getDatabaseInstance().storeModel().deleteStore(new Store(storeId));
                    emitter.onComplete();
                })
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    private SwipeToDeleteCallback getSwipeHandler(@NonNull Context context) {
        return new SwipeToDeleteCallback(context) {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                final Store store = storeListViewModel.getStoresValue().get(position);
                deleteStore(store.getStoreId());
            }
        };
    }
}
