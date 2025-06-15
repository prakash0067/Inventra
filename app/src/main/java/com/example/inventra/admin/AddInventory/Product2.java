package com.example.inventra.admin.AddInventory;

// Product.java
public class Product2 {
    private int product_id, category_id;
    private String product_name, description, barcodeText, image_path;
    private int quantity, new_quantity;
    private double price;

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public double getPrice() {
        return price;
    }

    public Product2(int product_id, int category_id, String product_name, String description, String barcodeText, int quantity, int new_quantity, double price) {
        this.product_id = product_id;
        this.category_id = category_id;
        this.product_name = product_name;
        this.description = description;
        this.barcodeText = barcodeText;
        this.quantity = quantity;
        this.new_quantity = new_quantity;
        this.price = price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Product2(int product_id, int category_id, String product_name, String description, String barcodeText, int quantity) {
        this.product_id = product_id;
        this.category_id = category_id;
        this.product_name = product_name;
        this.description = description;
        this.barcodeText = barcodeText;
        this.quantity = quantity;
    }

    public Product2(int product_id, int category_id, String product_name, String description, String barcodeText, int quantity, int new_quantity) {
        this.product_id = product_id;
        this.category_id = category_id;
        this.product_name = product_name;
        this.description = description;
        this.barcodeText = barcodeText;
        this.quantity = quantity;
        this.new_quantity = new_quantity;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
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

    public String getBarcodeText() {
        return barcodeText;
    }

    public void setBarcodeText(String barcodeText) {
        this.barcodeText = barcodeText;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getNew_quantity() {
        return new_quantity;
    }

    public void setNew_quantity(int new_quantity) {
        this.new_quantity = new_quantity;
    }

    // Getters and setters
    public int getProductId() { return product_id; }
    public String getProductName() { return product_name; }
    public int getQuantity() { return quantity; }
}
