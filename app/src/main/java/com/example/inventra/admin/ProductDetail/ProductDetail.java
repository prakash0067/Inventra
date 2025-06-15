package com.example.inventra.admin.ProductDetail;

public class ProductDetail {
    private int product_id;
    private String product_name;
    private String description;
    private int quantity;
    private String price;
    private String image_path;
    private String category_name;
    private String total_sales;

    public int getProductId() {
        return product_id;
    }

    public String getProductName() {
        return product_name;
    }

    public String getDescription() {
        return description;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getPrice() {
        return price;
    }

    public String getImagePath() {
        return image_path;
    }

    public String getCategoryName() {
        return category_name;
    }

    public String getTotalSales() {
        return total_sales;
    }
}
