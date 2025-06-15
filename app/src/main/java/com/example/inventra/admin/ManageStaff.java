package com.example.inventra.admin;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inventra.DBConnect.ApiClient;
import com.example.inventra.DBConnect.ApiService;
import com.example.inventra.R;
import com.example.inventra.Staff;
import com.example.inventra.StaffAdapter;
import com.example.inventra.admin.CartDetails.CartAdapter;
import com.example.inventra.admin.Staff.DeleteResponse;
import com.example.inventra.admin.Staff.InsertResponse;
import com.example.inventra.admin.Staff.StaffResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageStaff extends Fragment {

    private TextInputEditText staffNameInput, staffEmailInput, staffPasswordInput;
    private TextView addNewStaffView, viewAllStaffView;
    private MaterialButton btnSubmitStaff;
    private RecyclerView staffListView;
    private LinearLayout addStaffLayout, viewStaffLayout;

    private StaffAdapter staffAdapter;
    private List<Staff> staffList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_staff, container, false);

        staffNameInput = view.findViewById(R.id.inputStaffName);
        staffEmailInput = view.findViewById(R.id.inputStaffEmail);
        staffPasswordInput = view.findViewById(R.id.inputStaffPassword);

        addNewStaffView = view.findViewById(R.id.tabAddUser);
        viewAllStaffView = view.findViewById(R.id.tabViewUsers);

        staffListView = view.findViewById(R.id.recyclerViewStaff);
        staffListView.setLayoutManager(new LinearLayoutManager(getContext()));

        addStaffLayout = view.findViewById(R.id.addUserSection);
        viewStaffLayout = view.findViewById(R.id.viewUserSection);

        btnSubmitStaff = view.findViewById(R.id.btnSubmitStaff);

        // Add New Staff tab
        addNewStaffView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewStaffLayout.setVisibility(View.GONE);
                addStaffLayout.setVisibility(View.VISIBLE);

                addNewStaffView.setTextColor(Color.WHITE);
                addNewStaffView.setBackgroundResource(R.drawable.tab_selected_bg);

                // Reset View tab
                viewAllStaffView.setTextColor(Color.BLACK);
                viewAllStaffView.setBackgroundColor(Color.TRANSPARENT);
            }
        });

        // View Staff Details tab
        viewAllStaffView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewStaffLayout.setVisibility(View.VISIBLE);
                addStaffLayout.setVisibility(View.GONE);

                addNewStaffView.setTextColor(Color.BLACK);
                viewAllStaffView.setBackgroundResource(R.drawable.tab_selected_bg);

                // Reset View tab
                viewAllStaffView.setTextColor(Color.WHITE);
                addNewStaffView.setBackgroundColor(Color.TRANSPARENT);

                fetchStaffFromApi();
            }
        });

        // ====== add staff ======
        // Submit New Staff button
        btnSubmitStaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = staffNameInput.getText().toString().trim();
                String email = staffEmailInput.getText().toString().trim();
                String password = staffPasswordInput.getText().toString().trim();

                // Input validation
                if (name.isEmpty()) {
                    staffNameInput.setError("Name is required");
                    return;
                }
                if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    staffEmailInput.setError("Valid email is required");
                    return;
                }
                if (password.isEmpty()) {
                    staffPasswordInput.setError("Password is required");
                    return;
                }

                // api and api code
                Staff newStaff = new Staff(0, name, email, password);
                ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
                Call<InsertResponse> call = apiService.insertStaff(newStaff);

                call.enqueue(new Callback<InsertResponse>() {
                    @Override
                    public void onResponse(Call<InsertResponse> call, Response<InsertResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            InsertResponse insertResponse = response.body();
                            switch (insertResponse.getStatus()) {
                                case "success":
                                    Toast.makeText(getContext(), insertResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                    staffNameInput.setText("");
                                    staffEmailInput.setText("");
                                    staffPasswordInput.setText("");
                                    break;
                                case "exists":
                                    Toast.makeText(getContext(), "Email already registered", Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    Toast.makeText(getContext(), "Failed to add staff", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        } else {
                            Toast.makeText(getContext(), "Server error", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<InsertResponse> call, Throwable t) {
                        Log.d("==== Error ====",t.getMessage());
                        Toast.makeText(getContext(), "API error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        // delete staff swipe
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false; // We don't want drag-n-drop, only swipe
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Staff deletedStaff = staffAdapter.getItem(position);
                int staffId = deletedStaff.getId(); // assuming Staff class has getId()

                // Optional: confirmation dialog (recommended for real-world use)
                // Call delete API
                new AlertDialog.Builder(getContext())
                        .setTitle("Delete Staff")
                        .setMessage("Are you sure you want to delete this staff?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            deleteStaffFromApi(staffId, position);
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            staffAdapter.notifyItemChanged(position); // cancel swipe
                        })
                        .show();

            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {

                View foregroundView = ((StaffAdapter.StaffViewHolder) viewHolder).itemView.findViewById(R.id.foreground_view);
                View backgroundView = ((StaffAdapter.StaffViewHolder) viewHolder).itemView.findViewById(R.id.background_view);

                backgroundView.setVisibility(View.VISIBLE);

                // Translate foreground while swiping
                foregroundView.setTranslationX(dX);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(staffListView);


        return view;
    }

    private void fetchStaffFromApi() {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        Call<StaffResponse> call = apiService.getAllStaff();

        call.enqueue(new Callback<StaffResponse>() {
            @Override
            public void onResponse(Call<StaffResponse> call, Response<StaffResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getStatus().equals("success")) {
                    staffList = response.body().getStaff();

                    // Initialize and set adapter
                    staffAdapter = new StaffAdapter(staffList, getContext(), new StaffAdapter.OnDeleteListener() {
                        @Override
                        public void onDelete(int staffId, int position) {
                            // Optional: handle delete here if needed
                            // You can trigger delete API call here
                            Toast.makeText(getContext(), "Delete Staff ID: " + staffId, Toast.LENGTH_SHORT).show();
                        }
                    });

                    staffListView.setAdapter(staffAdapter);

                } else {
                    Toast.makeText(getContext(), "Failed to fetch staff", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<StaffResponse> call, Throwable t) {
                Toast.makeText(getContext(), "API error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void deleteStaffFromApi(int staffId, int position) {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        Call<DeleteResponse> call = apiService.deleteStaff(staffId); // make sure this method exists in ApiService

        call.enqueue(new Callback<DeleteResponse>() {
            @Override
            public void onResponse(Call<DeleteResponse> call, Response<DeleteResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DeleteResponse deleteResponse = response.body();

                    if ("success".equals(deleteResponse.getStatus())) {
                        staffAdapter.removeItem(position);
                        Toast.makeText(getContext(), deleteResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), deleteResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        staffAdapter.notifyItemChanged(position); // revert swipe
                    }
                } else {
                    Toast.makeText(getContext(), "Delete failed", Toast.LENGTH_SHORT).show();
                    staffAdapter.notifyItemChanged(position); // revert swipe
                }
            }

            @Override
            public void onFailure(Call<DeleteResponse> call, Throwable t) {
                Toast.makeText(getContext(), "API error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                staffAdapter.notifyItemChanged(position); // revert swipe
            }
        });
    }

}
