package com.example.inventra.admin.CartDetails;

public class PlaceOrderResponse {
    private String status;
    private String message;
    private int sale_id; // Optional, only for success

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public int getSaleId() {
        return sale_id;
    }
}
