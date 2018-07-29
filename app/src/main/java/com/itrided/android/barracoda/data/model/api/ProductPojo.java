package com.itrided.android.barracoda.data.model.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import android.os.Parcel;
import android.os.Parcelable;

import com.itrided.android.barracoda.data.Product;

import java.util.List;

public class ProductPojo implements Product, Parcelable {
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

    @Override
    public String getEan() {
        return ean;
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
    public String getWeight() {
        return weight;
    }

    @Override
    public String getImage() {
        return images != null && images.size() > 0 ? images.get(0) : "Image Unavailable";
    }

    @Override
    public String toString() {
        return "ean: " + ean +
                "\ntitle: " + name +
                "\ndescription: " + description +
                "\nweight: " + weight +
                "\nimage: " + images.get(0);
    }
}
