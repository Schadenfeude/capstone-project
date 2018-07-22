package com.itrided.android.barracoda.product;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.itrided.android.barracoda.data.model.api.ApiProduct;

public class ProductViewModel extends ViewModel {

    private final MutableLiveData<ApiProduct> product = new MutableLiveData<>();

    public void setProduct(@NonNull ApiProduct product) {
        this.product.setValue(product);
    }

    public MutableLiveData<ApiProduct> getProduct() {
        return product;
    }
}
