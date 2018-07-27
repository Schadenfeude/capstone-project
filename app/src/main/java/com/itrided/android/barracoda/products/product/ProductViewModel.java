package com.itrided.android.barracoda.products.product;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.itrided.android.barracoda.data.model.api.ProductPojo;

public class ProductViewModel extends ViewModel {

    private final MutableLiveData<ProductPojo> product = new MutableLiveData<>();

    public void setProduct(@NonNull ProductPojo product) {
        this.product.setValue(product);
    }

    public MutableLiveData<ProductPojo> getProduct() {
        return product;
    }
}
