package com.itrided.android.barracoda.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class ProductWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ProductWidgetDataProvider(this, intent);
    }
}
