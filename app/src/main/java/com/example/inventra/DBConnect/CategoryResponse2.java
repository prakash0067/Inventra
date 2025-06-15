package com.example.inventra.DBConnect;

import com.example.inventra.admin.Category;

import java.util.List;

public class CategoryResponse2 {
    private String status;
    private List<Category> categories; // This should match the "categories" field from the JSON response

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}

