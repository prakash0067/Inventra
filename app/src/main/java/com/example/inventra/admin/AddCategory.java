package com.example.inventra.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.inventra.DBConnect.ApiClient;
import com.example.inventra.DBConnect.ApiService;
import com.example.inventra.DBConnect.CategoryRequest;
import com.example.inventra.DBConnect.CategoryResponse2;
import com.example.inventra.DBConnect.CategoryResponse3;
import com.example.inventra.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.button.MaterialButton;

public class AddCategory extends Fragment {

    private TextInputEditText etCategoryName, etCategoryDescription;
    private MaterialButton btnSubmitCategory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_add_category, container, false);

        // Initialize views
        etCategoryName = rootView.findViewById(R.id.etCategoryName);
        etCategoryDescription = rootView.findViewById(R.id.etCategoryDescription);
        btnSubmitCategory = rootView.findViewById(R.id.btnSubmitCategory);

        // Set up the button listener
        btnSubmitCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the values from the input fields
                String categoryName = etCategoryName.getText().toString().trim();
                String categoryDescription = etCategoryDescription.getText().toString().trim();

                // Validate the input
                if (categoryName.isEmpty()) {
                    etCategoryName.setError("Category name is required!");
                } else if (categoryDescription.isEmpty()) {
                    etCategoryDescription.setError("Category description is required!");
                } else {
                    // Call the API to add the category
                    addCategoryToAPI(categoryName, categoryDescription);
                }
            }
        });

        return rootView;
    }

    // Method to call the API and add the category to the remote MySQL database
    private void addCategoryToAPI(String categoryName, String categoryDescription) {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);

        // Send the request to the server
        CategoryRequest category = new CategoryRequest(categoryName, categoryDescription);
        Call<CategoryResponse3> call = apiService.addCategory(category);

        // Handle the response from the API
        call.enqueue(new Callback<CategoryResponse3>() {
            @Override
            public void onResponse(Call<CategoryResponse3> call, Response<CategoryResponse3> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CategoryResponse3 categoryResponse = response.body();
                    if ("success".equals(categoryResponse.getStatus())) {
                        // Success message
                        Toast.makeText(getContext(), "Category added successfully!", Toast.LENGTH_SHORT).show();
                        // Clear the input fields after successful insertion
                        etCategoryName.setText("");
                        etCategoryDescription.setText("");
                    } else {
                        // Failure message
                        Toast.makeText(getContext(), "Failed to add category: " + categoryResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // If the response is not successful
                    Toast.makeText(getContext(), "Failed to add category. Please try again later.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<CategoryResponse3> call, Throwable t) {
                // Handle failure in calling the API
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
