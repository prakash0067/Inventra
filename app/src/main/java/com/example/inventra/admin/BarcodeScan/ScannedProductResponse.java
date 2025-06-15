package com.example.inventra.admin.BarcodeScan;

public class ScannedProductResponse {
    private boolean success;
    private ProductScan product;
    private String message;

    public boolean isSuccess() { return success; }
    public ProductScan getProduct() { return product; }
    public String getMessage() { return message; }
}
