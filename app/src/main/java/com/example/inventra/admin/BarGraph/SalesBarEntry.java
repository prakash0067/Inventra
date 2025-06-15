package com.example.inventra.admin.BarGraph;

public class SalesBarEntry {
    private String label;
    private float total;

    public String getLabel() {
        return label;
    }

    public float getTotal() {
        return total;
    }

    @Override
    public String toString() {
        return "SalesBarEntry{" +
                "label='" + label + '\'' +
                ", total=" + total +
                '}';
    }

}
