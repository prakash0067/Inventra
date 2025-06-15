package com.example.inventra.DBConnect;

public class CategoryRequest {
    private String categoryName;
    private String description;

    public CategoryRequest(String categoryName, String description) {
        this.categoryName = categoryName;
        this.description = description;
    }

    // Getters and setters (optional if using Gson)

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
