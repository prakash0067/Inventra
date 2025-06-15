package com.example.inventra.admin;

import static android.app.Activity.RESULT_OK;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.SearchView;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.inventra.AddStock;
import com.example.inventra.DBConnect.ApiClient;
import com.example.inventra.DBConnect.ApiService;
import com.example.inventra.admin.AddInventory.AddInventoryAdapter;
import com.example.inventra.admin.AddInventory.StockUpdateRequest;
import com.example.inventra.admin.AddInventory.StockUpdateResponse;
import com.example.inventra.DBHelper;
import com.example.inventra.R;
import com.example.inventra.admin.AddInventory.ProductSearchResponse;
import com.example.inventra.admin.AddInventory.Product2;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.Manifest;
import androidx.core.content.ContextCompat;

public class add_inventory extends Fragment {

    private SearchView searchView;
    private RecyclerView recyclerView;
    private AddInventoryAdapter adapter;
    private List<Product2> productList = new ArrayList<>(); // initially empty
    private DBHelper dbHelper;
    private TextInputEditText productIdInput, productNameInput, productQuantityInput, productNewStockInput;
    private MaterialButton btnSubmitNewStock;
    private LinearLayout searchLayout, inventoryLayout;
    private static final int REQUEST_CODE_SPEECH_INPUT = 101;
    private static final int REQUEST_PERMISSION_CODE = 100;

    public add_inventory() {
        // Required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_inventory, container, false);

        searchView = view.findViewById(R.id.searchProductsInv);
        recyclerView = view.findViewById(R.id.recyclerViewInv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new AddInventoryAdapter(productList, product -> {
            Intent intent = new Intent(getContext(), AddStock.class);
            intent.putExtra("productId", product.getProductId());
            intent.putExtra("productName", product.getProductName());
            intent.putExtra("productPrice", product.getPrice());
            intent.putExtra("productStock", product.getQuantity());
            intent.putExtra("productDesc", product.getDescription());
            intent.putExtra("productImage", product.getImage_path());
            startActivity(intent);
        });


        recyclerView.setAdapter(adapter);
        setupSearch();

        ImageView micIcon = view.findViewById(R.id.micIcon);
        micIcon.setOnClickListener(v -> askMicPermission());

        return view;
    }

    private void askMicPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_PERMISSION_CODE);
        } else {
            startVoiceInput();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startVoiceInput();
            } else {
                Toast.makeText(getContext(), "Mic permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), "Your device does not support speech input", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                SearchView searchView = requireView().findViewById(R.id.searchProductsInv);
                searchView.setQuery(result.get(0), true); // set and submit search
            }
        }
    }


    private void setupSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.isEmpty()) {
                    fetchProductsFromAPI(newText);
                } else {
                    recyclerView.setVisibility(View.GONE);
                }
                return true;
            }
        });
    }

    private void fetchProductsFromAPI(String query) {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);

        Map<String, String> body = new HashMap<>();
        body.put("searchQuery", query);

        apiService.searchProducts(body).enqueue(new Callback<ProductSearchResponse>() {
            @Override
            public void onResponse(Call<ProductSearchResponse> call, Response<ProductSearchResponse> response) {
                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                    List<Product2> results = response.body().getProducts();
                    Log.d("Data",results.toString());
                    if (!results.isEmpty()) {
                        recyclerView.setVisibility(View.VISIBLE);
                        productList.clear();
                        productList.addAll(results);
                        if (adapter != null) {
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        recyclerView.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<ProductSearchResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to load products", Toast.LENGTH_SHORT).show();
            }
        });
    }
}