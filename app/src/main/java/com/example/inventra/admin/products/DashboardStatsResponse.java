package com.example.inventra.admin.products;

import com.google.gson.annotations.SerializedName;

public class DashboardStatsResponse {

    @SerializedName("total_products")
    private int totalProducts;

    @SerializedName("total_quantity")
    private int totalQuantity;

    @SerializedName("product_growth")
    private double percentChangeProducts;

    @SerializedName("quantity_growth")
    private double percentChangeQuantity;
    @SerializedName("success")
    private String status;

    @SerializedName("error")
    private String error;

    public int getTotalProducts() {
        return totalProducts;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public double getPercentChangeProducts() {
        return percentChangeProducts;
    }

    public double getPercentChangeQuantity() {
        return percentChangeQuantity;
    }
}
