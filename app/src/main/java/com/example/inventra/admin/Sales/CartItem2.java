package com.example.inventra.admin.Sales;

import com.google.gson.annotations.SerializedName;

public class CartItem2 {
    @SerializedName("product_id")
    int productId;

    @SerializedName("quantity")
    int quantity;

    @SerializedName("price")
    double price;

    public CartItem2() {

    }
    public CartItem2(int productId, int quantity, double price) {
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}