package com.example.inventra;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.inventra.DBConnect.ApiClient;
import com.example.inventra.DBConnect.ApiService;
import com.example.inventra.admin.AddInventory.StockUpdateRequest;
import com.example.inventra.admin.AddInventory.StockUpdateResponse;
import com.example.inventra.admin.ProductImage.ProductImageResponse;
import com.example.inventra.admin.Profile.UploadResponse;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddStock extends AppCompatActivity {

    ImageView productImgView, changeImageView;
    TextInputEditText productNumberView, productNameView, productPriceView, productQtyView, newStockView, productDescriptionView;
    Button clearFormButton, saveStockButton;
    TextInputEditText dateTimeInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_stock);

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
            getSupportActionBar().setTitle("Add Inventory");
        }

        dateTimeInput = findViewById(R.id.dateTimeInput);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        dateTimeInput.setText(sdf.format(calendar.getTime()));

        dateTimeInput.setOnClickListener(v -> {
            // First open DatePicker
            new DatePickerDialog(AddStock.this, (view, year, month, dayOfMonth) -> {
                // Then TimePicker
                new TimePickerDialog(AddStock.this, (timeView, hourOfDay, minute) -> {
                    calendar.set(year, month, dayOfMonth, hourOfDay, minute);
                    dateTimeInput.setText(sdf.format(calendar.getTime()));
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });


        // product details auto fill

        productImgView = findViewById(R.id.productImage);
        changeImageView = findViewById(R.id.UpdateProductImg);
        productNumberView = findViewById(R.id.productIdView);
        productNameView = findViewById(R.id.productNameView);
        productPriceView = findViewById(R.id.productPriceView);
        productQtyView = findViewById(R.id.productQtyView);
        newStockView = findViewById(R.id.newStockView);
        productDescriptionView = findViewById(R.id.productDescriptionView);
        clearFormButton = findViewById(R.id.clearFormBtn);
        saveStockButton = findViewById(R.id.saveDataBtn);

        // product image code
        // Click listeners for profile image & camera icon
        View.OnClickListener openPickerMenu = v -> showImagePickerBottomSheet();
        changeImageView.setOnClickListener(openPickerMenu);

        // clear and save button code
        clearFormButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newStockView.setText("");
                productDescriptionView.setText("");
                updateCurrentDateTime();
            }
        });

        saveStockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPref = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
                int userId = sharedPref.getInt("UserId", -1);

                String newStockStr = newStockView.getText().toString().trim();
                String remarks = productDescriptionView.getText().toString().trim();
                String dateTime = dateTimeInput.getText().toString().trim();

                // Validation
                if (newStockStr.isEmpty()) {
                    newStockView.setError("Enter new stock quantity");
                    newStockView.requestFocus();
                    return;
                }

                int addedQty;
                try {
                    addedQty = Integer.parseInt(newStockStr);
                    if (addedQty <= 0) {
                        newStockView.setError("Quantity must be greater than zero");
                        return;
                    }
                } catch (NumberFormatException e) {
                    newStockView.setError("Invalid number");
                    return;
                }

                if (dateTime.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Date & Time is required", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Prepare request object
                StockUpdateRequest request = new StockUpdateRequest();
                request.setProductId(productNumberView.getText().toString().trim());
                request.setAddedQuantity(addedQty);
                request.setAddedBy(String.valueOf(userId));
                request.setRemarks(remarks);
                request.setAddedAt(dateTime);

                ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
                Call<StockUpdateResponse> call = apiService.updateProductStock(request);

                call.enqueue(new Callback<StockUpdateResponse>() {
                    @Override
                    public void onResponse(Call<StockUpdateResponse> call, Response<StockUpdateResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            newStockView.setText("");
                            productDescriptionView.setText("");
                            updateCurrentDateTime();
                        } else {
                            Toast.makeText(getApplicationContext(), "Update failed", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<StockUpdateResponse> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        Intent intent = getIntent();
        int productId = intent.getIntExtra("productId", -1);
        String name = intent.getStringExtra("productName");
        double price = intent.getDoubleExtra("productPrice", 0.0);
        int stock = intent.getIntExtra("productStock", 0);
        String desc = intent.getStringExtra("productDesc");
        String imagePath = intent.getStringExtra("productImage");

        if (intent != null && productId != -1) {
            productNumberView.setText(String.valueOf(productId));
            productNameView.setText(name);
            productPriceView.setText(String.valueOf(price));
            productQtyView.setText(String.valueOf(stock));
        } else {
            return;
        }

        // Load product image
        String fullImageUrl = ApiClient.BASE_IMAGE_URL + imagePath;
        Glide.with(this)
                .load(fullImageUrl)
                .placeholder(R.drawable.prod_default)
                .into(productImgView);

    }

    private void updateCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String currentDateTime = sdf.format(new Date());
        dateTimeInput.setText(currentDateTime);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // code for product image update
    private void showImagePickerBottomSheet() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View sheetView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_image_picker, null);

        TextView optionCamera = sheetView.findViewById(R.id.option_camera);
        TextView optionGallery = sheetView.findViewById(R.id.option_gallery);
        TextView optionCancel = sheetView.findViewById(R.id.option_cancel);

        optionCamera.setOnClickListener(v -> {
            ImagePicker.with(this)
                    .cameraOnly()
                    .cropSquare()
                    .compress(512)
                    .start();
            dialog.dismiss();
        });

        optionGallery.setOnClickListener(v -> {
            ImagePicker.with(this)
                    .galleryOnly()
                    .cropSquare()
                    .compress(512)
                    .start();
            dialog.dismiss();
        });

        optionCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.setContentView(sheetView);
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            String filePath = data.getStringExtra(ImagePicker.EXTRA_FILE_PATH);
            Log.d("Data ================",filePath);

            if (filePath != null) {
                File file = new File(filePath);
                uploadImageToServer(file);
            } else {
                Toast.makeText(this, "Unable to get file path", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Image selection cancelled or failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImageToServer(File file) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("product_image", file.getName(), requestFile);

        String pID = Objects.requireNonNull(productNumberView.getText()).toString();

        RequestBody productId = RequestBody.create(MediaType.parse("text/plain"), pID);
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);

        Call<ProductImageResponse> call = apiService.uploadProductImage(body, productId);
        call.enqueue(new Callback<ProductImageResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProductImageResponse> call, @NonNull Response<ProductImageResponse> response) {
                Log.d("=== Data ===",response.toString());
                if (response.isSuccessful() && response.body() != null) {
                    refreshImage(response.body().getImageUrl());
                    Toast.makeText(getApplicationContext(), "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        String error = response.errorBody().string(); // Get server response message
                        Log.e("UPLOAD_FAILED", error);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(AddStock.this, "Upload failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProductImageResponse> call, @NonNull Throwable t) {
                Toast.makeText(AddStock.this, "Upload error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void refreshImage(String imgName) {
        // Load product image
        String fullImageUrl = ApiClient.BASE_IMAGE_URL + imgName;
        Glide.with(this)
                .load(fullImageUrl)
                .placeholder(R.drawable.prod_default)
                .into(productImgView);
    }
}