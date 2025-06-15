package com.example.inventra.admin;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.inventra.DBConnect.ApiClient;
import com.example.inventra.DBConnect.ApiService;
import com.example.inventra.R;
import com.example.inventra.admin.BarcodeScan.ProductScan;
import com.example.inventra.admin.CartDetails.CartAddProductResponse;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductBottomSheetFragment extends BottomSheetDialogFragment {

    private static final String ARG_PRODUCT = "product";

    public static ProductBottomSheetFragment newInstance(ProductScan product) {
        ProductBottomSheetFragment fragment = new ProductBottomSheetFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PRODUCT, product);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.product_bottom_sheet, container, false);

        ProductScan product = (ProductScan) getArguments().getSerializable(ARG_PRODUCT);
        if (product == null) return view;

        TextView name = view.findViewById(R.id.bottomSheetName);
        TextView price = view.findViewById(R.id.bottomSheetPrice);
        TextView stock = view.findViewById(R.id.bottomSheetStock);
        ImageView image = view.findViewById(R.id.bottomSheetImage);
        Button addToCart = view.findViewById(R.id.btnAddToCart);

        name.setText(product.getProduct_name());
        price.setText("Price: ₹" + product.getPrice());
        stock.setText("In Stock: " + product.getQuantity());

        Glide.with(this).load(ApiClient.BASE_IMAGE_URL + product.getImage_path())
                .placeholder(R.drawable.prod_default)
                .into(image);

        SharedPreferences sharedPref = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        int userId = sharedPref.getInt("UserId", -1);

        addToCart.setOnClickListener(v -> {
            // function to add product to cart using API
            int productId = product.getProduct_id();
            int quantity = 1;

            ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
            Call<CartAddProductResponse> call = apiService.addToCart(userId, productId, quantity);

            call.enqueue(new Callback<CartAddProductResponse>() {
                @Override
                public void onResponse(Call<CartAddProductResponse> call, Response<CartAddProductResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        CartAddProductResponse apiResponse = response.body();
                        if (apiResponse.getStatus().equalsIgnoreCase("success")) {
                            Toast.makeText(getContext(), "✅ " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            // Optional: refresh cart icon badge or cart screen
                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                dismiss();
                            }, 500);

                        } else {
                            Toast.makeText(getContext(), "⚠️ " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Server Error", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<CartAddProductResponse> call, Throwable t) {
                    Toast.makeText(getContext(), "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        return view;
    }
}
