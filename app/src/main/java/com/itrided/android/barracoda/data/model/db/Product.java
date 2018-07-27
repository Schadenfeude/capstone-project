package com.itrided.android.barracoda.data.model.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.List;

@Entity
public class Product implements Parcelable {

    @PrimaryKey
    @NonNull
    private String ean;

    private String name;

    private String description;

    private String weight;

    private List<String> images;

    protected Product(Parcel in) {
        ean = in.readString();
        name = in.readString();
        description = in.readString();
        weight = in.readString();
        images = in.createStringArrayList();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ean);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(weight);
        dest.writeStringList(images);
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

    public List<String> getImages() {
        return images;
    }

    @Override
    public String toString() {
        return "ean: " + ean +
                "title: " + name +
                "description: " + description +
                "weight: " + weight +
                "images: " + images;
    }
}

