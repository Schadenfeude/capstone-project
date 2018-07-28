package com.itrided.android.barracoda.products;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.LiveDataReactiveStreams;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.itrided.android.barracoda.BarraCodaApp;
import com.itrided.android.barracoda.data.BarraCodaDb;
import com.itrided.android.barracoda.data.model.db.ProductEntry;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class ProductListViewModel extends ViewModel {

    private final BarraCodaDb barraCodaDb = BarraCodaApp.getDatabaseInstance();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private LiveData<List<ProductEntry>> products = new MutableLiveData<>();

    public ProductListViewModel() {
        loadProducts();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }

//    public void addProduct(@NonNull Product product) {
//        this.products.getValue().add(product);
//    }

    public LiveData<List<ProductEntry>> getProducts() {
        return products;
    }

    private void loadProducts() {
        products =
                LiveDataReactiveStreams.fromPublisher(
                        barraCodaDb.productModel().getAllProducts()
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread()));
    }
}
