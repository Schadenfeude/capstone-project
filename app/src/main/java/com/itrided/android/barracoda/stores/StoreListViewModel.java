package com.itrided.android.barracoda.stores;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.LiveDataReactiveStreams;
import android.arch.lifecycle.ViewModel;

import com.itrided.android.barracoda.BarraCodaApp;
import com.itrided.android.barracoda.data.BarraCodaDb;
import com.itrided.android.barracoda.data.model.db.Store;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class StoreListViewModel extends ViewModel {
    private BarraCodaDb barraCodaDb = BarraCodaApp.getDatabaseInstance();
    private LiveData<List<Store>> stores;

    public StoreListViewModel() {
        stores = LiveDataReactiveStreams.fromPublisher(
                barraCodaDb.storeModel().getAllStores()
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread()));
    }

    public LiveData<List<Store>> getStores() {
        return stores;
    }

    public List<Store> getStoresValue() {
        return stores.getValue();
    }
}
