package com.itrided.android.barracoda.data.model.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.itrided.android.barracoda.data.model.Product;

@Entity
public class ProductEntry implements Product {

    @PrimaryKey
    @NonNull
    private String id;

    private String name;

    private String description;

    private String price;

    public ProductEntry(@NonNull String id, String name, String description, String price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public ProductEntry(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "id: " + id +
                "\ntitle: " + name +
                "\ndescription: " + description +
                "\nprice: " + price;
    }
}

