package com.itrided.android.barracoda.data.model.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;
import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface StoreDao {
    @Query("SELECT * FROM Store WHERE storeId = :storeId")
    Single<Store> getStore(@NonNull String storeId);

    @Query("SELECT * FROM Store")
    Flowable<List<Store>> getAllStores();

    @Query("SELECT * FROM Store")
    Single<List<Store>> getAllStoresSync();
    
    @Insert(onConflict = IGNORE)
    void addStore(@NonNull Store store);

    @Update(onConflict = REPLACE)
    void updateStore(@NonNull Store store);

    @Delete
    void deleteStore(@NonNull Store store);
}
