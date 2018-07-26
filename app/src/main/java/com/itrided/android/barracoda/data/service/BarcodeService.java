package com.itrided.android.barracoda.data.service;

import android.support.annotation.NonNull;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BarcodeService {
    String productLookupURL = "prod/trial/lookup";
    String barcodeQueryParam = "upc";

    @GET(productLookupURL)
    Single<ResponseBody> getProduct(@Query(barcodeQueryParam) @NonNull String barcode);
}
