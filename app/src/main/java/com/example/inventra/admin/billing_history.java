package com.example.inventra.admin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.inventra.DBConnect.ApiClient;
import com.example.inventra.DBConnect.ApiService;
import com.example.inventra.DBHelper;
import com.example.inventra.R;
import com.example.inventra.admin.Sales.BillingResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class billing_history extends Fragment {

    private RecyclerView billingRecyclerView;
    private BillingAdapter adapter;
    private TextView noDataText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_billing_history, container, false);
        billingRecyclerView = view.findViewById(R.id.billingRecyclerView);
        noDataText = view.findViewById(R.id.noDataText);
        billingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        apiService.getSalesHistory().enqueue(new Callback<BillingResponse>() {
            @Override
            public void onResponse(Call<BillingResponse> call, Response<BillingResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().status) {
                    List<BillingModel> billingList = response.body().data;

                    if (billingList.isEmpty()) {
                        billingRecyclerView.setVisibility(View.GONE);
                        noDataText.setVisibility(View.VISIBLE);
                    } else {
                        billingRecyclerView.setVisibility(View.VISIBLE);
                        noDataText.setVisibility(View.GONE);
                        adapter = new BillingAdapter(billingList);
                        billingRecyclerView.setAdapter(adapter);
                    }
                } else {
                    noDataText.setText("Failed to load data.");
                    noDataText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<BillingResponse> call, Throwable t) {
                noDataText.setText("Error: " + t.getMessage());
                noDataText.setVisibility(View.VISIBLE);
            }
        });


        return view;
    }
}