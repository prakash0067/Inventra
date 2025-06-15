package com.example.inventra.admin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.example.inventra.BarcodeScannerActivity;
import com.example.inventra.DBConnect.ApiClient;
import com.example.inventra.DBConnect.ApiService;
import com.example.inventra.DBHelper;
import com.example.inventra.R;
import com.example.inventra.admin.CartDetails.CartAddProductResponse;
import com.example.inventra.admin.SearchProducts.SearchResponse;
import com.example.inventra.admin.SearchProducts.Product3;
import com.example.inventra.admin.ProductAdapter2;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class search extends Fragment {

    private SearchView searchView;
    private RecyclerView recyclerView;
    private ProductAdapter2 adapter;
    private List<Product3> productList = new ArrayList<>();

    public search() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchView = view.findViewById(R.id.searchViewProducts);
        recyclerView = view.findViewById(R.id.recyclerViewProducts);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        adapter = new ProductAdapter2(productList, this::showProductBottomSheet);
        recyclerView.setAdapter(adapter);

        ImageView scanIcon = view.findViewById(R.id.scanIcon);
        scanIcon.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), BarcodeScannerActivity.class);
            startActivity(intent);
        });

        setupSearch();

        return view;
    }

    private void setupSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false; // Not needed
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.isEmpty()) {
                    fetchProductsFromApi(newText);
                } else {
                    productList.clear();
                    adapter.notifyDataSetChanged();
                    recyclerView.setVisibility(View.GONE);
                }
                return true;
            }
        });
    }

    private void fetchProductsFromApi(String query) {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);

        JsonObject json = new JsonObject();
        json.addProperty("searchQuery", query);

        apiService.searchProducts(json).enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                if (response.isSuccessful() && response.body() != null &&
                        "success".equalsIgnoreCase(response.body().getStatus())) {

                    List<Product3> results = response.body().getProducts();

                    if (results != null && !results.isEmpty()) {
                        productList.clear();
                        productList.addAll(results);
                        adapter.notifyDataSetChanged();
                        recyclerView.setVisibility(View.VISIBLE);
                    } else {
                        recyclerView.setVisibility(View.GONE);
                    }
                } else {
                    recyclerView.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "No products found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                recyclerView.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showProductBottomSheet(Product3 product) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = LayoutInflater.from(getContext()).inflate(R.layout.product_bottom_sheet, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        ImageView img = bottomSheetView.findViewById(R.id.bottomSheetImage);
        TextView name = bottomSheetView.findViewById(R.id.bottomSheetName);
        TextView stock = bottomSheetView.findViewById(R.id.bottomSheetStock);
        TextView price = bottomSheetView.findViewById(R.id.bottomSheetPrice);
        TextView desc = bottomSheetView.findViewById(R.id.bottomSheetDesc);
        MaterialButton btnAddToCart = bottomSheetView.findViewById(R.id.btnAddToCart);

        Glide.with(requireContext())
                .load(ApiClient.BASE_IMAGE_URL + product.getImage_path())
                .placeholder(R.drawable.prod_default)
                .error(R.drawable.prod_default)
                .into(img);

        name.setText(product.getProductName());
        price.setText("₹" + product.getPrice());
        desc.setText(product.getDescription());
        stock.setText("In Stock: " + product.getQuantity() + " Pcs");
        SharedPreferences sharedPref = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        int userId = sharedPref.getInt("UserId", -1);

        btnAddToCart.setOnClickListener(v -> {
            int productId = product.getProductId();
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
                        } else {
                            Toast.makeText(getContext(), "⚠️ " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Server Error", Toast.LENGTH_SHORT).show();
                    }
                    bottomSheetDialog.dismiss();
                }

                @Override
                public void onFailure(Call<CartAddProductResponse> call, Throwable t) {
                    Toast.makeText(getContext(), "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    bottomSheetDialog.dismiss();
                }
            });
        });

        bottomSheetDialog.show();
    }

}
