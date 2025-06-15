package com.example.inventra.admin.BarGraph;

import java.util.List;

public class SalesResponseBar {
    private boolean success;
    private List<SalesBarEntry> data;

    public boolean isSuccess() {
        return success;
    }

    public List<SalesBarEntry> getData() {
        return data;
    }

    @Override
    public String toString() {
        return "SalesResponseBar{" +
                "success=" + success +
                ", data=" + data +
                '}';
    }

}
