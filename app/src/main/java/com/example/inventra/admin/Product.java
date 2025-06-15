package com.example.inventra.admin;

import com.google.gson.annotations.SerializedName;

public class Product {
    private int productId, categoryId, quantity;
    private String productName, description, barcodeText, createdOn;
    @SerializedName("image_path")
    private String image_path;
    private double price;

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public int getProductId() {
        return productId;
    }

    public Product(int categoryId, int productId, int quantity, double price, String productName, String description, String barcodeText) {
        this.categoryId = categoryId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.productName = productName;
        this.description = description;
        this.barcodeText = barcodeText;
    }

    public Product(int productId, String productName, double price, int quantity, String barcodeText) {
        this.productId = productId;
        this.quantity = quantity;
        this.productName = productName;
        this.barcodeText = barcodeText;
        this.price = price;
    }

    public Product(int productId, int quantity, String productName, String barcodeText, double price, String imgPath) {
        this.productId = productId;
        this.quantity = quantity;
        this.productName = productName;
        this.barcodeText = barcodeText;
        this.price = price;
        this.image_path = imgPath;
    }

    public Product() {
    }

    public Product(int productId, String productName, String description, double price, int quantity, String barcodeText, String imgPath) {
        this.productId = productId;
        this.quantity = quantity;
        this.productName = productName;
        this.description = description;
        this.barcodeText = barcodeText;
        this.price = price;
        this.image_path = imgPath;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getBarcodeText() {
        return barcodeText;
    }

    public void setBarcodeText(String barcodeText) {
        this.barcodeText = barcodeText;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
