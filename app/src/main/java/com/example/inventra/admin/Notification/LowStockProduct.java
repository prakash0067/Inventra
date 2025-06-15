package com.example.inventra.admin.Notification;

import com.google.gson.annotations.SerializedName;

public class LowStockProduct {
    @SerializedName("product_name")
    private String productName;

    @SerializedName("quantity")
    private String quantity;

    public String getProductName() {
        return productName;
    }

    public String getQuantity() {
        return quantity;
    }

    public int getQuantityAsInt() {
        try {
            return Integer.parseInt(quantity);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
