package com.example.inventra.admin.ProductImage;

import com.google.gson.annotations.SerializedName;

public class ProductImageResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("image_url")
    private String imageUrl;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}