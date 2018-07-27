package com.itrided.android.barracoda;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import android.app.Application;

import com.itrided.android.barracoda.data.model.api.ProductPojo;
import com.itrided.android.barracoda.data.service.BackupBarcodeService;
import com.itrided.android.barracoda.data.service.BarcodeService;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.lang.reflect.Type;

import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class BarraCodaApp extends Application {
    private static final String BARCODE_SERVICE_BASE_URL = "https://api.upcitemdb.com/";
    private static final String BACKUP_BARCODE_SERVICE_BASE_URL = "https://api.upcdatabase.org/";

    private static Gson gsonService;
    private static BarcodeService barcodeService;
    //todo implement
    private static BackupBarcodeService backupBarcodeService;

    @Override
    public void onCreate() {
        super.onCreate();

        barcodeService = createBarcodeService();
        gsonService = createGsonService();
    }

    public static BarcodeService getBarcodeService() {
        return barcodeService;
    }

    public static Gson getGsonService() {
        return gsonService;
    }

    private static Gson createGsonService() {
        return new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(ProductPojo.class, new BarcodeJsonDeserializer<ProductPojo>())
                .create();
    }

    private static BarcodeService createBarcodeService() {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BARCODE_SERVICE_BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build();

        return retrofit.create(BarcodeService.class);
    }

    private static class BarcodeJsonDeserializer<T> implements JsonDeserializer<T> {
        private static final String JSON_INNER_ELEMENT = "items";
        private static final int JSON_ITEMS_FIRST = 0;

        @Override
        public T deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            try {
                final JsonElement jsonArray = json.getAsJsonObject().get(JSON_INNER_ELEMENT);
                final JsonElement content = jsonArray.getAsJsonArray().get(JSON_ITEMS_FIRST);

                // Deserialize it. You use a new instance of Gson to avoid infinite recursion
                // to this deserializer
                return new Gson().fromJson(content, type);
            } catch (IndexOutOfBoundsException e) {
                return null;
            }
        }
    }
}
