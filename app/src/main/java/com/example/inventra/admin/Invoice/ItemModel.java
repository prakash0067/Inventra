package com.example.inventra.admin.Invoice;

import com.google.gson.annotations.SerializedName;

// ItemModel.java
public class ItemModel {
    @SerializedName("product_name")
    public String productName;

    @SerializedName("product_price")
    public double price;

    @SerializedName("qty_purchased")
    public int quantity;

    @SerializedName("product_image")
    public String imagePath;

    public String getProductName() {
        return productName;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getImagePath() {
        return imagePath;
    }
}
