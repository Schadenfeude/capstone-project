package com.itrided.android.barracoda.data.model.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Store {
    @PrimaryKey
    @NonNull
    String storeId;

    public Store(@NonNull String storeId) {
        this.storeId = storeId;
    }

    public String getStoreId() {
        return storeId;
    }

}
