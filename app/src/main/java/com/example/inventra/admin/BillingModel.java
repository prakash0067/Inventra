package com.example.inventra.admin;

public class BillingModel {
    int billNumber,totalItems;
    String customerName, saleDate, totalAmount, saleTime;

    public BillingModel(int billNumber, String customerName, String totalAmount, String saleDate, String saleTime, int totalItems) {
        this.billNumber = billNumber;
        this.customerName = customerName;
        this.saleDate = saleDate;
        this.totalAmount = totalAmount;
        this.saleTime = saleTime;
        this.totalItems = totalItems;
    }

    // Getters
    public int getBillNumber() { return billNumber; }
    public String getCustomerName() { return customerName; }
    public String getSaleDate() { return saleDate; }
    public String getTotalAmount() { return totalAmount; }
    public String getSalesTime() {
        return saleTime;
    }
    public int getTotalItems() {
        return totalItems;
    }
}
