package com.itrided.android.barracoda.data.model.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class ProductPojo implements Parcelable {
    @SerializedName("ean")
    @Expose
    private String ean;

    @SerializedName("title")
    @Expose
    private String name;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("weight")
    @Expose
    private String weight;

    @SerializedName("images")
    @Expose
    private List<String> images;

    protected ProductPojo(Parcel in) {
        ean = in.readString();
        name = in.readString();
        description = in.readString();
        weight = in.readString();
        images = in.createStringArrayList();
    }

    public static final Creator<ProductPojo> CREATOR = new Creator<ProductPojo>() {
        @Override
        public ProductPojo createFromParcel(Parcel in) {
            return new ProductPojo(in);
        }

        @Override
        public ProductPojo[] newArray(int size) {
            return new ProductPojo[size];
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
