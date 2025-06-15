package com.example.inventra.admin.Invoice;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ItemsInvoiceResponse {
    public String status;
    public List<ItemModel> items; // not 'products'
}

