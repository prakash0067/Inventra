package com.example.inventra.admin.Sales;
import com.google.gson.annotations.SerializedName;
public class SalesGrowthResponse {
    @SerializedName("sales_growth_percent")
    private double salesGrowthPercent;

    public double getSalesGrowthPercent() {
        return salesGrowthPercent;
    }
}