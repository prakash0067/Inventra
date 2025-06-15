package com.example.inventra.admin.RecentInvoice;

import com.google.gson.annotations.SerializedName;

public class RecentInvoice {
    @SerializedName("sale_id")
    private int salesId;
    @SerializedName("customer_name")
    private String customerName;
    @SerializedName("created_at")
    private String dateTime;
    @SerializedName("total_amount")
    private String totalAmount;
    @SerializedName("payment_status")
    private String status; // Paid or Pending

    public RecentInvoice(String customerName, String dateTime, String totalAmount, String status) {
        this.customerName = customerName;
        this.dateTime = dateTime;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    // Getters...

    public RecentInvoice(int salesId, String customerName, String dateTime, String totalAmount, String status) {
        this.salesId = salesId;
        this.customerName = customerName;
        this.dateTime = dateTime;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public int getSalesId() {
        return salesId;
    }

    public void setSalesId(int salesId) {
        this.salesId = salesId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
