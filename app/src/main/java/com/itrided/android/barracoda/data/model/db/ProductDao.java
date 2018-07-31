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
public interface ProductDao {

    @Query("SELECT * FROM ProductEntry WHERE id = :id")
    Single<ProductEntry> getProduct(String id);

    @Query("SELECT * FROM ProductEntry")
    Flowable<List<ProductEntry>> getAllProducts();

    @Query("SELECT * FROM ProductEntry")
    Single<List<ProductEntry>> getAllProductsSync();

    @Insert(onConflict = IGNORE)
    void addProduct(@NonNull ProductEntry product);

    @Update(onConflict = REPLACE)
    void updateProduct(@NonNull ProductEntry product);

    @Delete
    void deleteProduct(@NonNull ProductEntry product);
}
