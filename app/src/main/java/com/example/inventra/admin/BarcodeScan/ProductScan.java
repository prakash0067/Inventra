package com.example.inventra.admin.BarcodeScan;

import java.io.Serializable;

import java.io.Serializable;

public class ProductScan implements Serializable {
    private int product_id;
    private String product_name;
    private String barcode;
    private int quantity;
    private double price;
    private String image_path;

    // Getters
    public int getProduct_id() { return product_id; }
    public String getProduct_name() { return product_name; }
    public String getBarcode() { return barcode; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public String getImage_path() { return image_path; }
}