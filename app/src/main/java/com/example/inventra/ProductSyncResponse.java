package com.example.inventra;

import com.example.inventra.admin.Product;
import java.util.List;

public class ProductSyncResponse {
    private String status;
    private List<Product> products;

    public String getStatus() { return status; }
    public List<Product> getProducts() { return products; }
}
