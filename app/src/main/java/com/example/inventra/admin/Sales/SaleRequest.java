package com.example.inventra.admin.Sales;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SaleRequest {
    @SerializedName("customer_name")
    public String customerName;

    @SerializedName("customer_mobile")
    public String customerMobile;

    @SerializedName("payment_status")
    public String paymentStatus;

    @SerializedName("payment_method")
    public String paymentMethod;

    @SerializedName("cart_items")
    public List<CartItem2> cartItems;

    // constructor, getters, setters
    public SaleRequest() {}
    public SaleRequest(String customerName, String customerMobile, String paymentStatus, String paymentMethod, List<CartItem2> cartItems) {
        this.customerName = customerName;
        this.customerMobile = customerMobile;
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;
        this.cartItems = cartItems;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerMobile() {
        return customerMobile;
    }

    public void setCustomerMobile(String customerMobile) {
        this.customerMobile = customerMobile;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public List<CartItem2> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItem2> cartItems) {
        this.cartItems = cartItems;
    }
}
