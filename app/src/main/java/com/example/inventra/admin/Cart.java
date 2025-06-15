package com.example.inventra.admin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.inventra.DBConnect.ApiService;
import com.example.inventra.DBConnect.ApiClient;
import com.example.inventra.DBHelper;
import com.example.inventra.OrderSummaryActivity;
import com.example.inventra.R;
import com.example.inventra.admin.CartDetails.CartAdapter;
import com.example.inventra.admin.CartDetails.CartItem;
import com.example.inventra.admin.CartDetails.CartQtyUpdateResponse;
import com.example.inventra.admin.CartDetails.CartResponse;
import com.example.inventra.admin.CartDetails.CartTotalResponse;
import com.example.inventra.admin.Sales.CartItem2;
import com.example.inventra.admin.Sales.SaleRequest;
import com.example.inventra.admin.Sales.SalesResponse2;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Cart extends Fragment implements CartAdapter.CartUpdateListener {

    private List<CartItem> cartItemList;
    private CartAdapter cartAdapter;
    FrameLayout loaderOverlay;
    TextView totalPriceTextView;

    @Override
    public void onCartUpdated() {
        SharedPreferences sharedPref = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        int userId = sharedPref.getInt("UserId", -1);
        fetchCartTotal(userId);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_cart);
        totalPriceTextView = view.findViewById(R.id.total_price_textview);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loaderOverlay = view.findViewById(R.id.loaderOverlay);
        loadCartItems();

        // delete button using gestures
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.LEFT) {
                    int position = viewHolder.getAdapterPosition();
                    CartItem item = cartItemList.get(position); // your cart list

                    loaderOverlay.setVisibility(View.VISIBLE);
                    ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
                    Call<CartQtyUpdateResponse> call = apiService.updateCartQuantity(
                            item.getUserId(),
                            item.getProductId(),
                            item.getCartId(),
                            "remove"
                    );

                    call.enqueue(new Callback<CartQtyUpdateResponse>() {
                        @Override
                        public void onResponse(Call<CartQtyUpdateResponse> call, Response<CartQtyUpdateResponse> response) {
                            if (response.isSuccessful() && response.body() != null && response.body().getStatus().equals("success")) {
                                cartItemList.remove(position);
                                cartAdapter.notifyItemRemoved(position);
                                Toast.makeText(getContext(), "Item removed", Toast.LENGTH_SHORT).show();
                                // ✅ Update total
                                onCartUpdated();
                                loaderOverlay.setVisibility(View.GONE);

                                // Show "Cart is Empty" message if list becomes empty
                                MaterialCardView emptyMessage = requireView().findViewById(R.id.cartEmptyMessage);
                                if (cartItemList.isEmpty()) {
                                    emptyMessage.setVisibility(View.VISIBLE);
                                }
                            } else {
                                Toast.makeText(getContext(), "Failed to delete", Toast.LENGTH_SHORT).show();
                                cartAdapter.notifyItemChanged(position); // rollback
                                loaderOverlay.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onFailure(Call<CartQtyUpdateResponse> call, Throwable t) {
                            Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
                            cartAdapter.notifyItemChanged(position); // rollback
                        }
                    });
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {

                View foregroundView = ((CartAdapter.CartViewHolder) viewHolder).itemView.findViewById(R.id.foreground_view);
                View backgroundView = ((CartAdapter.CartViewHolder) viewHolder).itemView.findViewById(R.id.background_view);

                backgroundView.setVisibility(View.VISIBLE);

                // Translate foreground while swiping
                foregroundView.setTranslationX(dX);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        Button checkoutButton = view.findViewById(R.id.proceed_button);

        checkoutButton.setOnClickListener(v -> {
            if (cartItemList == null || cartItemList.isEmpty()) {
                Toast.makeText(getContext(), "Cart is empty. Cannot proceed to checkout.", Toast.LENGTH_SHORT).show();
            } else {
                double totalAmount = 0;
                for (CartItem item : cartItemList) {
                    totalAmount += item.getPrice() * item.getPurchaseQuantity();
                }

                // Launch OrderSummaryActivity
                Intent intent = new Intent(getContext(), OrderSummaryActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    // method to update cart's total amount
    private void fetchCartTotal(int userId) {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        Call<CartTotalResponse> call = apiService.getCartTotal(userId);

        call.enqueue(new Callback<CartTotalResponse>() {
            @Override
            public void onResponse(Call<CartTotalResponse> call, Response<CartTotalResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CartTotalResponse data = response.body();
                    if ("success".equals(data.getStatus())) {
                        double total = data.getTotal();
                        totalPriceTextView.setText("₹" + total); // replace with your TextView ID
                    } else {
                        Toast.makeText(getContext(), "Error: " + data.getStatus(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to get total", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CartTotalResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCartItems();
    }

    private void loadCartItems() {
        loaderOverlay.setVisibility(View.VISIBLE);

        SharedPreferences sharedPref = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        int userId = sharedPref.getInt("UserId", -1);

        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        Call<CartResponse> call = apiService.getCartItems(userId);

        call.enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getStatus().equals("success")) {
                    List<CartItem> items = response.body().getProducts();
                    cartItemList = items;
                    cartAdapter = new CartAdapter(getContext(), cartItemList, Cart.this);
                    RecyclerView recyclerView = requireView().findViewById(R.id.recycler_view_cart);
                    recyclerView.setAdapter(cartAdapter);

                    double total = 0;
                    for (CartItem item : items) {
                        total += item.getPrice() * item.getPurchaseQuantity();
                    }
                    totalPriceTextView.setText("₹" + total);
                    loaderOverlay.setVisibility(View.GONE);

                    MaterialCardView emptyMessage = requireView().findViewById(R.id.cartEmptyMessage);
                    emptyMessage.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
                } else {
                    Toast.makeText(getContext(), "Cart is empty or error fetching data", Toast.LENGTH_SHORT).show();
                    MaterialCardView emptyMessage = requireView().findViewById(R.id.cartEmptyMessage);
                    emptyMessage.setVisibility(View.VISIBLE);
                    loaderOverlay.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}
