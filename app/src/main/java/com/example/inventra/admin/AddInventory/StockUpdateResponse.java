package com.example.inventra.admin.AddInventory;

import com.google.gson.annotations.SerializedName;

public class StockUpdateResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("product_id")
    private int productId;

    @SerializedName("added_quantity")
    private int addedQuantity;

    // Constructor
    public StockUpdateResponse(String status, String message, int productId, int addedQuantity) {
        this.status = status;
        this.message = message;
        this.productId = productId;
        this.addedQuantity = addedQuantity;
    }

    // Getters and Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getAddedQuantity() {
        return addedQuantity;
    }

    public void setAddedQuantity(int addedQuantity) {
        this.addedQuantity = addedQuantity;
    }
}