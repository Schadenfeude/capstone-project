package com.itrided.android.barracoda.data.model.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.itrided.android.barracoda.data.Product;

@Entity
public class ProductEntry implements Product {

    @PrimaryKey
    @NonNull
    private String ean;

    private String name;

    private String description;

    private String weight;

    private String image;

    public ProductEntry(@NonNull String ean, String name, String description, String weight, String image) {
        this.ean = ean;
        this.name = name;
        this.description = description;
        this.weight = weight;
        this.image = image;
    }

    public ProductEntry(Product product) {
        this.ean = product.getEan();
        this.name = product.getName();
        this.description = product.getDescription();
        this.weight = product.getWeight();
        this.image = product.getImage();
    }

    public String getEan() {
        return ean;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getWeight() {
        return weight;
    }

    public String getImage() {
        return image;
    }

    @Override
    public String toString() {
        return "ean: " + ean +
                "\ntitle: " + name +
                "\ndescription: " + description +
                "\nweight: " + weight +
                "\nimage: " + image;
    }
}

