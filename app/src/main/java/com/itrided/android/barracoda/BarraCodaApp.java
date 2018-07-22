package com.itrided.android.barracoda;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import android.app.Application;
import android.util.Log;

import com.itrided.android.barracoda.data.model.api.ApiProduct;
import com.itrided.android.barracoda.data.service.BarcodeService;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.lang.reflect.Type;

import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BarraCodaApp extends Application {
    private static final String BARCODE_SERVICE_BASE_URL = "https://api.upcitemdb.com/";
    private static final String JSON_INNER_ELEMENT = "items";

    private static BarcodeService recipeService;

    @Override
    public void onCreate() {
        super.onCreate();

        recipeService = createBarcodeService();
    }

    public static BarcodeService getRecipeService() {
        return recipeService;
    }

    private static BarcodeService createBarcodeService() {
        final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(ApiProduct.class, new BarcodeJSONDeserializer<ApiProduct>())
                .create();

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BARCODE_SERVICE_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build();

        return retrofit.create(BarcodeService.class);
    }

    private static class BarcodeJSONDeserializer<T> implements JsonDeserializer<T> {

        private static final String TAG = BarcodeJSONDeserializer.class.getSimpleName();

        @Override
        public T deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            final JsonElement jsonArray = json.getAsJsonObject().get(JSON_INNER_ELEMENT);
            final JsonElement content = jsonArray.getAsJsonArray().get(0);

            // Deserialize it. You use a new instance of Gson to avoid infinite recursion
            // to this deserializer
            return new Gson().fromJson(content, type);
        }
    }
}
