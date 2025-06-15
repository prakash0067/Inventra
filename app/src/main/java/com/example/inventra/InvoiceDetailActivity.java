package com.example.inventra;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inventra.DBConnect.ApiClient;
import com.example.inventra.DBConnect.ApiService;
import com.example.inventra.admin.Invoice.InvoiceAdapter;
import com.example.inventra.admin.Invoice.ItemModel;
import com.example.inventra.admin.Invoice.ItemsInvoiceResponse;
import com.example.inventra.admin.Invoice.SalesInvoiceResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InvoiceDetailActivity extends AppCompatActivity {

    DBHelper dbHelper;
    TextView invoiceNumber, customerName, customerContactNo, salesDate, salesTime, grandTotal, subTotal;
    String saleId;
    RecyclerView allItemsList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge drawing
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_invoice_detail);

        // Set status bar color and icon visibility
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.transparent)); // Transparent for edge-to-edge
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Enable back button and set title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Invoice Summary");
        }

        // initializing all values
        invoiceNumber = findViewById(R.id.invoiceNumberView);
        customerName = findViewById(R.id.customerNameView);
        customerContactNo = findViewById(R.id.customerPhoneView);
        salesDate = findViewById(R.id.salesDateView);
        salesTime = findViewById(R.id.salesTimeView);
        grandTotal = findViewById(R.id.grandTotalView);
        subTotal = findViewById(R.id.subTotalView);
        allItemsList = findViewById(R.id.allItemsList);

        Intent intent = getIntent();
        int id = intent.getIntExtra("sale_id", 0);

        if (id != 0) {
            saleId = String.valueOf(id);
            String text = "Invoice No. " + id;
            invoiceNumber.setText(text);

            showProductDetails(id);
        } else {
            Log.e("InvoiceDetailActivity", "No sale_id found in Intent");
        }

        InvoiceAdapter invoiceAdapter = new InvoiceAdapter(new ArrayList<>());
        allItemsList.setLayoutManager(new LinearLayoutManager(this));
        allItemsList.setAdapter(invoiceAdapter);

        showProductDetails(id);
    }


    private void showProductDetails(int salesId) {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);

        Log.d("API Called ====",apiService.toString());
        // Fetch Customer Info
        apiService.getSalesInvoiceDetails(salesId, "sales").enqueue(new Callback<SalesInvoiceResponse>() {
            @Override
            public void onResponse(Call<SalesInvoiceResponse> call, Response<SalesInvoiceResponse> response) {
                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().status)) {
                    Map<String, String> customer = response.body().sale; // corrected key

                    if (customer != null) {
                        customerName.setText(customer.get("customer_name"));
                        customerContactNo.setText(customer.get("customer_mobile_number"));
                        salesDate.setText(customer.get("sales_date"));
                        salesTime.setText(customer.get("sales_time"));

                        String totalAmount = customer.get("total_amount");
                        subTotal.setText("₹" + totalAmount);
                        grandTotal.setText("₹" + totalAmount);
                    } else {
                        Log.e("InvoiceDetail", "Customer data is null");
                    }
                } else {
                    Log.e("API Error", "Status: " + (response.body() != null ? response.body().status : "null"));
                    Log.e("API Error", "Response code: " + response.code());
                    Log.e("API Error", "Message: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<SalesInvoiceResponse> call, Throwable t) {
                Log.e("InvoiceDetail", "Failed to fetch customer info: " + t.getMessage());
            }
        });

        // Fetch Purchased Items
        apiService.getItemInvoiceDetails(salesId, "items").enqueue(new Callback<ItemsInvoiceResponse>() {
            @Override
            public void onResponse(Call<ItemsInvoiceResponse> call, Response<ItemsInvoiceResponse> response) {
                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().status)) {
                    List<ItemModel> items = response.body().items;
                    allItemsList.setLayoutManager(new LinearLayoutManager(InvoiceDetailActivity.this));
                    allItemsList.setAdapter(new InvoiceAdapter(items));
                }
            }

            @Override
            public void onFailure(Call<ItemsInvoiceResponse> call, Throwable t) {
                Log.e("InvoiceDetail", "Failed to fetch items: " + t.getMessage());
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}