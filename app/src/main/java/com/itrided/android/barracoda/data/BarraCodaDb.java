package com.itrided.android.barracoda.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

import com.itrided.android.barracoda.data.model.db.ProductEntry;
import com.itrided.android.barracoda.data.model.db.ProductDao;
import com.itrided.android.barracoda.data.model.db.Store;
import com.itrided.android.barracoda.data.model.db.StoreDao;

@Database(entities = {ProductEntry.class, Store.class}, version = 2)
public abstract class BarraCodaDb extends RoomDatabase {

    private static final String DATABASE_NAME = "BARRA_CODA_DB";
    private static BarraCodaDb INSTANCE;

    public abstract ProductDao productModel();

    public abstract StoreDao storeModel();

    public static BarraCodaDb getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room
                    .databaseBuilder(context.getApplicationContext(), BarraCodaDb.class, DATABASE_NAME)
                    //todo add migration strategy
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}