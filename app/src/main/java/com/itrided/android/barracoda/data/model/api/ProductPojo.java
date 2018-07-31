package com.itrided.android.barracoda.data.model.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import android.os.Parcel;
import android.os.Parcelable;

import com.itrided.android.barracoda.data.model.Product;

import java.util.List;

public class ProductPojo implements Product, Parcelable {
    @SerializedName("upcnumber")
    @Expose
    private String id;

    @SerializedName("title")
    @Expose
    private String name;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("msrp")
    @Expose
    private String price;

    protected ProductPojo(Parcel in) {
        id = in.readString();
        name = in.readString();
        description = in.readString();
        price = in.readString();
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
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(price);
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
        return "ean: " + id +
                "\ntitle: " + name +
                "\ndescription: " + description +
                "\nweight: " + price;
    }
}
