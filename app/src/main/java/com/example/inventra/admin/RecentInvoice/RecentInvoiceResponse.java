package com.example.inventra.admin.RecentInvoice;

import java.util.List;

public class RecentInvoiceResponse {
    private boolean success;
    private List<RecentInvoice> data;

    public boolean isSuccess() { return success; }
    public List<RecentInvoice> getData() { return data; }
}
