package com.example.inventra;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.inventra.DBConnect.ApiClient;
import com.example.inventra.DBConnect.ApiService;
import com.example.inventra.admin.CartDetails.CartItem;
import com.example.inventra.admin.CartDetails.CartItemAdapter;
import com.example.inventra.admin.CartDetails.CartResponse;
import com.example.inventra.admin.CartDetails.PlaceOrderResponse;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Callback;

public class OrderSummaryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CartItemAdapter adapter;
    private List<CartItem> cartItems = new ArrayList<>();
    private TextView totalAmountSummary;
    Button confirmOrderBtn;
    TextInputEditText customerNameField, customerPhoneField;
    RadioGroup paymentMethodGroup, paymentStatusGroup;
    CheckBox sendBillCheckbox;
    FrameLayout successOrderAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_summary);

        recyclerView = findViewById(R.id.order_summary_recyclerview);
        totalAmountSummary = findViewById(R.id.total_amount_summary);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        successOrderAnim = findViewById(R.id.loaderOverlay);

        // Set transparent status bar
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.transparent));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Order Summary");
        }

        SharedPreferences sharedPref = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        int userId = sharedPref.getInt("UserId", -1);

        // initializing fields
        confirmOrderBtn = findViewById(R.id.confirm_order_button);
        customerNameField = findViewById(R.id.customer_name);
        customerPhoneField = findViewById(R.id.customer_phone);
        paymentMethodGroup = findViewById(R.id.payment_method_group);
        sendBillCheckbox = findViewById(R.id.send_bill_checkbox);
        paymentStatusGroup = findViewById(R.id.payment_status_group);

        // confirm order button click
        confirmOrderBtn.setOnClickListener(v -> {
            String name = customerNameField.getText().toString().trim();
            String phone = customerPhoneField.getText().toString().trim();
            int selectedPaymentId = paymentMethodGroup.getCheckedRadioButtonId();
            int selectedPaymentStatus = paymentStatusGroup.getCheckedRadioButtonId();

            if (name.isEmpty()) {
                customerNameField.setError("Customer name is required");
                customerNameField.requestFocus();
                return;
            }

            if (phone.isEmpty() || phone.length() != 10 || !phone.matches("[6-9][0-9]{9}")) {
                customerPhoneField.setError("Enter valid 10-digit phone number");
                customerPhoneField.requestFocus();
                return;
            }

            if (selectedPaymentId == -1) {
                Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedPaymentStatus == -1) {
                Toast.makeText(this, "Please select a payment status", Toast.LENGTH_SHORT).show();
                return;
            }

            // ✅ Get selected text
            RadioButton selectedPaymentMethod = findViewById(selectedPaymentId);
            RadioButton selectedStatus = findViewById(selectedPaymentStatus);

            String paymentMethod = selectedPaymentMethod.getText().toString(); // "Cash" or "UPI"
            String paymentStatus = selectedStatus.getText().toString();       // "Paid" or "Unpaid"

            if (sendBillCheckbox.isChecked()) {
                Toast.makeText(this, "Bill will be sent to " + phone, Toast.LENGTH_SHORT).show();
            }

            // ✅ Now you can use: name, phone, paymentMethod, paymentStatus
             placeOrder(userId, name, phone, paymentMethod, paymentStatus);
        });

        fetchCartItems();
    }


    private void fetchCartItems() {
        SharedPreferences sharedPref = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        int userId = sharedPref.getInt("UserId", -1);

        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        Call<CartResponse> call = apiService.getCartItemsOrderSummary(userId);

        call.enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getStatus().equals("success")) {
                    cartItems = response.body().getProducts();
                    adapter = new CartItemAdapter(OrderSummaryActivity.this, cartItems);
                    recyclerView.setAdapter(adapter);
                    updateTotal();
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                Toast.makeText(OrderSummaryActivity.this, "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTotal() {
        int total = 0;
        for (CartItem item : cartItems) {
            total += item.getPrice() * item.getPurchaseQuantity();
        }
        totalAmountSummary.setText("Total: ₹" + total);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void placeOrder(int userId, String name, String phone, String paymentMethod, String paymentStatus) {
        successOrderAnim.setVisibility(View.VISIBLE);
        confirmOrderBtn.setEnabled(false);
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        Call<PlaceOrderResponse> call = apiService.placeOrder(userId, name, phone, paymentMethod, paymentStatus);

        call.enqueue(new Callback<PlaceOrderResponse>() {
            @Override
            public void onResponse(Call<PlaceOrderResponse> call, Response<PlaceOrderResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PlaceOrderResponse result = response.body();
                    if ("success".equalsIgnoreCase(result.getStatus())) {
                        Toast.makeText(getApplicationContext(), "Order placed! Sale ID: " + result.getSaleId(), Toast.LENGTH_SHORT).show();

                        new android.os.Handler().postDelayed(() -> {
                            successOrderAnim.setVisibility(View.GONE);
                            Intent intent = new Intent(OrderSummaryActivity.this, InvoiceDetailActivity.class);
                            intent.putExtra("sale_id", result.getSaleId());
                            startActivity(intent);
                            finish();
                        }, 2000); // 2 seconds
                    }
                    else {
                        successOrderAnim.setVisibility(View.GONE);
                        confirmOrderBtn.setEnabled(false);
                        Toast.makeText(getApplicationContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    successOrderAnim.setVisibility(View.GONE);
                    confirmOrderBtn.setEnabled(false);
                    Toast.makeText(getApplicationContext(), "Unexpected server error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PlaceOrderResponse> call, Throwable t) {
                successOrderAnim.setVisibility(View.GONE);
                confirmOrderBtn.setEnabled(false);
                Toast.makeText(getApplicationContext(), "Request failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // or finish()
        return true;
    }

}