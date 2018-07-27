package com.itrided.android.barracoda.data.model.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.support.annotation.NonNull;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;
import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface ProductDao {

    @Query("SELECT * FROM Product WHERE ean = :ean")
    Product loadProductByEan(int ean);

    @Query("SELECT * FROM Product")
    LiveData<List<Product>> findAllProducts();

    @Query("SELECT * FROM Product")
    List<Product> findAllProductsSync();

    @Insert(onConflict = IGNORE)
    void addProduct(@NonNull Product product);

    @Update(onConflict = REPLACE)
    void updateProduct(@NonNull Product product);

    @Delete
    void deleteProduct(@NonNull Product product);
}
