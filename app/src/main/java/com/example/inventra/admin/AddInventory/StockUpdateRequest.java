package com.example.inventra.admin.AddInventory;

import com.google.gson.annotations.SerializedName;

public class StockUpdateRequest {
    @SerializedName("product_id")
    private String productId;

    @SerializedName("added_quantity")
    private int addedQuantity;

    @SerializedName("added_by")
    private String addedBy;

    @SerializedName("remarks")
    private String remarks;

    @SerializedName("added_at")
    private String addedAt;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getAddedQuantity() {
        return addedQuantity;
    }

    public void setAddedQuantity(int addedQuantity) {
        this.addedQuantity = addedQuantity;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    public String getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(String addedAt) {
        this.addedAt = addedAt;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
