package com.itrided.android.barracoda.data.service;

import android.support.annotation.NonNull;

import com.itrided.android.barracoda.BuildConfig;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface BackupBarcodeService {
    String productLookupURL = "product/{id}/" + BuildConfig.UPCDB_API_KEY;

    @GET(productLookupURL)
    Single<ResponseBody> getProductBackup(@Path("id") @NonNull String barcode);
}
