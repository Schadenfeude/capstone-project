package com.itrided.android.barracoda.products.product;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.itrided.android.barracoda.data.Product;

public class ProductViewModel extends ViewModel {

    private final MutableLiveData<Product> product = new MutableLiveData<>();

    public void setProduct(@NonNull Product product) {
        this.product.setValue(product);
    }

    public MutableLiveData<Product> getProduct() {
        return product;
    }
}
