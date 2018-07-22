package com.itrided.android.barracoda.data.service;

import android.support.annotation.NonNull;

import com.itrided.android.barracoda.data.model.api.ApiProduct;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BarcodeService {
    String productLookupURL = "prod/trial/lookup";
    String barcodeQueryParam = "upc";

    @GET(productLookupURL)
    Single<ApiProduct> getProduct(@Query(barcodeQueryParam) @NonNull String barcode);
}
