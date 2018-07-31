package com.itrided.android.barracoda.widget;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;

import com.itrided.android.barracoda.BarraCodaApp;
import com.itrided.android.barracoda.MainActivity;
import com.itrided.android.barracoda.R;
import com.itrided.android.barracoda.data.model.Product;
import com.itrided.android.barracoda.data.model.db.ProductEntry;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;

public class ProductWidget extends AppWidgetProvider {

    public static final String EXTRA_WIDGET_REQUESTED_PRODUCT = "widgetRequestedProduct";
    public static final String EXTRA_WIDGET_PRODUCT = "widgetProduct";
    public static final String EXTRA_WIDGET_ID = "widgetId";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            ProductWidgetConfigureActivity.deleteRecipePreference(context, appWidgetId);
        }
    }

    @SuppressLint("CheckResult")
    static void updateWidget(@NonNull Context context, @NonNull AppWidgetManager appWidgetManager,
                             int widgetId) {
        final CharSequence productName = ProductWidgetConfigureActivity.loadRecipePreference(context, widgetId);
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_product_list);

        BarraCodaApp.getDatabaseInstance().productModel().getAllProductsSync()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<ProductEntry>>() {
                    @SuppressLint("CheckResult")
                    @Override
                    public void onSuccess(List<ProductEntry> results) {
                        if (results.size() == 0) {
                            return;
                        }

                        final Intent intent = new Intent(context, ProductWidgetService.class);
                        setupMainViews(context, views, productName);
//                        Observable.fromIterable(results)
//                                .filter(product -> product.getName().contentEquals(productName))
//                                .flatMapIterable(Recipe::getIngredients)
//                                .map(ingredient -> formatProduct(context, ingredient))
//                                .toList()
//                                .subscribeWith(new SingleObserver<List<String>>() {
//                                    @Override
//                                    public void onSubscribe(Disposable d) {
//
//                                    }
//
//                                    @Override
//                                    public void onSuccess(List<String> ingredients) {
//                                        intent.putExtra(EXTRA_WIDGET_PRODUCT, new ArrayList<>(ingredients));
//                                        intent.putExtra(EXTRA_WIDGET_ID, widgetId);
//
//                                        views.setRemoteAdapter(R.id.recipe_list_lv, intent);
//                                        appWidgetManager.notifyAppWidgetViewDataChanged(widgetId, R.id.recipe_list_lv);
//                                        appWidgetManager.updateAppWidget(widgetId, views);
//                                    }
//
//                                    @Override
//                                    public void onError(Throwable e) {
//                                        e.printStackTrace();
//                                    }
//                                });
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                });

        appWidgetManager.updateAppWidget(widgetId, views);
    }

    private static void setupMainViews(@NonNull Context context, @NonNull RemoteViews views,
                                       @Nullable CharSequence productName) {

        views.setTextViewText(R.id.recipe_name_tv,
                String.format(context.getString(R.string.widget_product_title), productName));
        views.setEmptyView(R.id.recipe_list_lv, R.id.loading);

        final Intent listIntent = new Intent(context, MainActivity.class);
        final PendingIntent listPendingIntent = PendingIntent
                .getActivity(context, 0, listIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.recipe_list_lv, listPendingIntent);

        final Intent titleIntent = new Intent(context, MainActivity.class);
        titleIntent.putExtra(EXTRA_WIDGET_REQUESTED_PRODUCT, productName);
        final PendingIntent titlePendingIntent = PendingIntent
                .getActivity(context, 0, titleIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.recipe_name_tv, titlePendingIntent);
    }

    private static String formatProduct(@NonNull Context context, @NonNull Product product) {
        return String.format(context.getString(R.string.widget_product_item_text),
                product.getName(),
                product.getDescription(),
                product.getPrice());
    }
}
