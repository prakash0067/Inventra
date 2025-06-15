package com.example.inventra.admin.SearchProducts;
import java.util.List;
import com.example.inventra.admin.Product;

public class SearchResponse {
    private String status;
    private List<Product3> products;

    public String getStatus() {
        return status;
    }

    public List<Product3> getProducts() {
        return products;
    }
}