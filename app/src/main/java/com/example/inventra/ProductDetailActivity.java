package com.example.inventra;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.example.inventra.DBConnect.ApiClient;
import com.example.inventra.DBConnect.ApiService;
import com.example.inventra.admin.CartDetails.CartAddProductResponse;
import com.example.inventra.admin.ProductDetail.ProductDetail;
import com.example.inventra.admin.ProductDetail.ProductDetailResponse;
import com.example.inventra.admin.products.Product2;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.animation.ValueAnimator;
import android.graphics.drawable.ColorDrawable;

public class ProductDetailActivity extends AppCompatActivity {

    TextView txtName, txtPrice, txtCategory, txtDescription, txtTotalSales, txtAvailQty;
    ImageView productImage;
    MaterialButton btnAddToCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_detail);

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
            getSupportActionBar().setTitle("Product Details");
        }

        // Bind Views
        txtName = findViewById(R.id.productName);
        txtPrice = findViewById(R.id.productPrice);
        txtCategory = findViewById(R.id.productCategory);
        txtDescription = findViewById(R.id.productDescription);
        txtTotalSales = findViewById(R.id.totalSales);
        productImage = findViewById(R.id.productImage);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        txtAvailQty = findViewById(R.id.availableQty);

        // Get product ID from intent
        int productId = getIntent().getIntExtra("product_id", -1);

        if (productId != -1) {
            fetchProductDetails(productId);
        } else {
            Toast.makeText(this, "Invalid Product ID", Toast.LENGTH_SHORT).show();
        }

        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToCart(productId);
            }
        });
    }

    private void fetchProductDetails(int productId) {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        Call<ProductDetailResponse> call = apiService.getProductById(productId);

        LinearLayout imageContainer = findViewById(R.id.imageContainer);

        call.enqueue(new Callback<ProductDetailResponse>() {
            @Override
            public void onResponse(Call<ProductDetailResponse> call, Response<ProductDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    ProductDetail product = response.body().getProduct();
                    txtName.setText(product.getProductName());
                    txtPrice.setText("₹" + product.getPrice());
                    txtCategory.setText(product.getCategoryName());
                    txtDescription.setText(product.getDescription());
                    txtTotalSales.setText("Total Sales : " + product.getTotalSales());
                    txtAvailQty.setText(String.valueOf(product.getQuantity()));

                    if (product.getQuantity() > 10) {
                        txtAvailQty.setTextColor(getResources().getColor(R.color.green));
                    } else {
                        txtAvailQty.setTextColor(getResources().getColor(R.color.red));
                    }

                    String imageUrl = ApiClient.BASE_IMAGE_URL + product.getImagePath();

                    Glide.with(ProductDetailActivity.this)
                            .asBitmap()
                            .load(imageUrl)
                            .placeholder(R.drawable.prod_default)
                            .error(R.drawable.prod_default)
                            .into(new com.bumptech.glide.request.target.CustomTarget<android.graphics.Bitmap>() {
                                @Override
                                public void onResourceReady(android.graphics.Bitmap resource, com.bumptech.glide.request.transition.Transition<? super android.graphics.Bitmap> transition) {
                                    if (resource != null && !resource.isRecycled() && resource.getWidth() > 0 && resource.getHeight() > 0) {
                                        productImage.setImageBitmap(resource);

                                        // Safe Palette generation
                                        try {
                                            int cropWidth = resource.getWidth() / 10;  // take 1/10th of width
                                            int cropHeight = resource.getHeight() / 10; // take 1/10th of height
                                            Bitmap topLeftCorner = Bitmap.createBitmap(resource, 0, 0, cropWidth, cropHeight);


                                            Palette.from(topLeftCorner).generate(palette -> {
                                                int defaultColor = ContextCompat.getColor(ProductDetailActivity.this, R.color.white);
                                                int cornerColor = palette.getLightMutedColor(palette.getMutedColor(defaultColor));

                                                int currentColor;
                                                if (imageContainer.getBackground() instanceof ColorDrawable) {
                                                    currentColor = ((ColorDrawable) imageContainer.getBackground()).getColor();
                                                } else {
                                                    currentColor = defaultColor; // fallback color
                                                }

                                                ValueAnimator colorAnimation = ValueAnimator.ofArgb(currentColor, cornerColor);
                                                colorAnimation.setDuration(500); // 0.5 seconds
                                                colorAnimation.addUpdateListener(animator -> {
                                                    imageContainer.setBackgroundColor((int) animator.getAnimatedValue());
                                                });
                                                colorAnimation.start();
                                            });

                                        } catch (IllegalArgumentException e) {
                                            Toast.makeText(ProductDetailActivity.this, "Error in color extraction", Toast.LENGTH_SHORT).show();
                                            e.printStackTrace();
                                        }
                                    } else {
                                        Toast.makeText(ProductDetailActivity.this, "Invalid image resource for color extraction", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onLoadCleared(android.graphics.drawable.Drawable placeholder) {
                                    // Optional
                                }
                            });

                } else {
                    Toast.makeText(ProductDetailActivity.this, "Product not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductDetailResponse> call, Throwable t) {
                Toast.makeText(ProductDetailActivity.this, "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addToCart(int productId) {

        SharedPreferences sharedPref = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        int userId = sharedPref.getInt("UserId", -1);
        int quantity = 1;

        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        Call<CartAddProductResponse> call = apiService.addToCart(userId, productId, quantity);

        call.enqueue(new Callback<CartAddProductResponse>() {
            @Override
            public void onResponse(Call<CartAddProductResponse> call, Response<CartAddProductResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CartAddProductResponse apiResponse = response.body();
                    if (apiResponse.getStatus().equalsIgnoreCase("success")) {
                        Toast.makeText(ProductDetailActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        // Optional: refresh cart icon badge or cart screen
                    } else {
                        Toast.makeText(ProductDetailActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ProductDetailActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CartAddProductResponse> call, Throwable t) {
                Toast.makeText(ProductDetailActivity.this, "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // or finish()
        return true;
    }
}