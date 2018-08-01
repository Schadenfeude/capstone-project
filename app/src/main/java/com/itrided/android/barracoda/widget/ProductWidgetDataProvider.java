package com.itrided.android.barracoda.widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.itrided.android.barracoda.R;
import com.itrided.android.barracoda.products.ProductListFragment;

import java.util.ArrayList;

import static com.itrided.android.barracoda.widget.ProductWidget.EXTRA_WIDGET_ID;
import static com.itrided.android.barracoda.widget.ProductWidget.EXTRA_WIDGET_PRODUCT;
import static com.itrided.android.barracoda.widget.ProductWidget.EXTRA_WIDGET_REQUESTED_PRODUCT;

public class ProductWidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {

    private int appWidgetId;
    private Context context;
    private ArrayList<String> productInfo;

    public ProductWidgetDataProvider(Context context, Intent intent) {
        this.context = context;
        this.appWidgetId = intent.getIntExtra(EXTRA_WIDGET_ID, 0);
        this.productInfo = intent.getStringArrayListExtra(EXTRA_WIDGET_PRODUCT);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return productInfo == null ? 0 : productInfo.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.item_widget_product);
        final CharSequence productName = ProductWidgetConfigureActivity
                .loadProductPreference(context, appWidgetId);
        final Intent intent = new Intent(context, ProductListFragment.class);

        intent.putExtra(EXTRA_WIDGET_REQUESTED_PRODUCT, productName);
        remoteViews.setOnClickFillInIntent(R.id.product_tv, intent);

        remoteViews.setTextViewText(R.id.product_tv, productInfo.get(position));

        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return new RemoteViews(context.getPackageName(), R.layout.item_widget_product);
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
