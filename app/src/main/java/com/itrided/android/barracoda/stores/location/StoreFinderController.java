package com.itrided.android.barracoda.stores.location;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.itrided.android.barracoda.BarraCodaApp;
import com.itrided.android.barracoda.R;
import com.itrided.android.barracoda.data.BarraCodaDb;
import com.itrided.android.barracoda.data.model.db.Store;
import com.itrided.android.barracoda.permissions.PermissionManager;
import com.itrided.android.barracoda.stores.StoreListAdapter;
import com.itrided.android.barracoda.stores.StoreListViewModel;

import java.util.ArrayList;

public class StoreFinderController implements LifecycleObserver,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = StoreFinderController.class.getSimpleName();

    public static final int RC_PLACE_PICKER = 200;

    private final Context context;
    private final Fragment fragment;
    private final Activity activity;
    private final GoogleApiClient googleClient;
    private final BarraCodaDb barraCodaDb = BarraCodaApp.getDatabaseInstance();
    private final StoreListAdapter adapter;

    private StoreListViewModel storeListViewModel;

    public StoreFinderController(Fragment fragment, StoreListAdapter adapter) {
        this.context = fragment.getContext();
        this.activity = fragment.getActivity();
        this.fragment = fragment;
        this.adapter = adapter;
        this.storeListViewModel = ViewModelProviders.of(fragment).get(StoreListViewModel.class);

        if (!PermissionManager.checkPermission(context, PermissionManager.Permission.FINE_LOCATION)) {
            PermissionManager.requestPermission(fragment, PermissionManager.Permission.FINE_LOCATION);
        }

        this.googleClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected: ");
        refreshStoreData();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        connectClient();
    }

    public void addPlace(@NonNull View view) {
        if (!PermissionManager.checkPermission(context, PermissionManager.Permission.FINE_LOCATION)) {
            final Snackbar snackbar = Snackbar
                    .make(view, R.string.no_location_permission_message, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.give_permission, v -> PermissionManager.requestPermission(
                            fragment, PermissionManager.Permission.FINE_LOCATION));

            snackbar.show();
            return;
        }
        try {
            // Start a new Activity for the Place Picker API, this will trigger {@code #onActivityResult}
            // when a place is selected or with the user cancels.
            final PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            final Intent intent = builder.build(activity);

            fragment.startActivityForResult(intent, RC_PLACE_PICKER);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            Log.e(TAG, String.format("GooglePlayServices Not Available [%s]", e.getMessage()));
        } catch (Exception e) {
            Log.e(TAG, String.format("PlacePicker Exception: %s", e.getMessage()));
        }
    }

    private void refreshStoreData() {
        setupResultObserver();
    }

    private void setupResultObserver() {
        storeListViewModel.getStores().observe(fragment, storesList -> {
            if (adapter == null || storesList == null || storesList.isEmpty()) {
                return;
            }

            final ArrayList<String> guids = new ArrayList<>(storesList.size());
            for (Store store : storesList) {
                guids.add(store.getStoreId());
            }

            final PendingResult<PlaceBuffer> placeResult =
                    Places.GeoDataApi.getPlaceById(googleClient, guids.toArray(new String[guids.size()]));

            placeResult.setResultCallback(adapter::setPlaces);
        });
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void disconnectClient() {
        if (googleClient.isConnected()) {
            googleClient.disconnect();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void connectClient() {
        if (!googleClient.isConnected()) {
            googleClient.connect();
        }
    }
}
