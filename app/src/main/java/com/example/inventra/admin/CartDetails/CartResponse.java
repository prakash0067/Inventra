package com.example.inventra.admin.CartDetails;

import java.util.List;

public class CartResponse {
    private String status;
    private List<CartItem> products;

    public String getStatus() { return status; }
    public List<CartItem> getProducts() { return products; }
}