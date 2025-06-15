package com.example.inventra.admin.products;
import com.google.gson.annotations.SerializedName;

public class Product2 {
    @SerializedName("product_id")
    private int productId;

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    private String product_name,image_path;
    private String description;
    private double price;
    private int quantity;
    private String barcode;

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String imageUrl) {
        this.image_path = imageUrl;
    }

    private int category_id;

    // Getters
    public String getProductName() {
        return product_name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }
}
