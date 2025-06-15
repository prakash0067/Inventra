package com.example.inventra.admin;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.inventra.AvailableProductsAdapter;
import com.example.inventra.DBConnect.ApiService;
import com.example.inventra.DBConnect.ApiClient;
import com.example.inventra.R;
import com.example.inventra.admin.products.DashboardStatsResponse;
import com.example.inventra.admin.products.Product2;
import com.example.inventra.admin.products.ProductResponse2;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductFragment extends Fragment {

    private RecyclerView recyclerView;
    private AvailableProductsAdapter adapter;
    private LottieAnimationView lottieLoader;
    private TextView totalProductField, stockInHandField, percentChangeProduct, percentChangeQty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewProducts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        lottieLoader = view.findViewById(R.id.lottieLoader);
        lottieLoader.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        totalProductField = view.findViewById(R.id.totalProductField);
        stockInHandField = view.findViewById(R.id.stockInHandField);
        percentChangeProduct = view.findViewById(R.id.percentChangeProduct);
        percentChangeQty = view.findViewById(R.id.percentChangeQty);

        if (isConnectedToInternet()) {
            fetchDashboardStats();
            fetchProductsFromAPI();
        } else {
            lottieLoader.setVisibility(View.GONE);
            Toast.makeText(getContext(), "No internet connection. Please connect to the internet.", Toast.LENGTH_LONG).show();
        }

        return view;
    }

    private boolean isConnectedToInternet() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }

    private void fetchProductsFromAPI() {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        Call<ProductResponse2> call = apiService.getAllProducts();

        call.enqueue(new Callback<ProductResponse2>() {
            @Override
            public void onResponse(Call<ProductResponse2> call, Response<ProductResponse2> response) {
                lottieLoader.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                if (response.isSuccessful() && response.body() != null) {
                    List<Product2> productList = response.body().getProducts();
                    adapter = new AvailableProductsAdapter(getContext(), productList);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(getContext(), "Failed to load products", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductResponse2> call, Throwable t) {
                lottieLoader.setVisibility(View.GONE);
                Toast.makeText(getContext(), "API Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchDashboardStats() {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        Call<DashboardStatsResponse> call = apiService.getDashboardStats();

        call.enqueue(new Callback<DashboardStatsResponse>() {
            @Override
            public void onResponse(Call<DashboardStatsResponse> call, Response<DashboardStatsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DashboardStatsResponse stats = response.body();

                    totalProductField.setText(String.valueOf(stats.getTotalProducts()));
                    stockInHandField.setText(String.valueOf(stats.getTotalQuantity()));

                    String productChange = String.format("%+.2f%%", stats.getPercentChangeProducts());
                    String qtyChange = String.format("%+.2f%%", stats.getPercentChangeQuantity());

                    percentChangeProduct.setText(productChange);
                    percentChangeQty.setText(qtyChange);

                    // Color logic (optional)
                    if (stats.getPercentChangeProducts() < 0) {
                        percentChangeProduct.setBackgroundResource(R.drawable.rounded_red_background);
                    } else {
                        percentChangeProduct.setBackgroundResource(R.drawable.rounded_green_background);
                    }

                    if (stats.getPercentChangeQuantity() < 0) {
                        percentChangeQty.setBackgroundResource(R.drawable.rounded_red_background);
                    } else {
                        percentChangeQty.setBackgroundResource(R.drawable.rounded_blue_background);
                    }

                } else {
                    Toast.makeText(getContext(), "Failed to load dashboard stats", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DashboardStatsResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Stats API Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
