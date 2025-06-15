package com.example.inventra.admin.CartDetails;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.inventra.DBConnect.ApiClient;
import com.example.inventra.DBConnect.ApiService;
import com.example.inventra.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItems;
    private Context context;
    private CartUpdateListener cartUpdateListener;

    public interface CartUpdateListener {
        void onCartUpdated(); // This will be called when cart changes
    }

    public CartAdapter(Context context, List<CartItem> items, CartUpdateListener listener) {
        this.context = context;
        this.cartItems = items;
        this.cartUpdateListener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.name.setText(item.getProductName());
        holder.price.setText("₹" + item.getPrice());
        holder.quantity.setText(String.valueOf(item.getPurchaseQuantity()));

        String imageUrl = ApiClient.BASE_IMAGE_URL + item.getProductImage();
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.prod_default)
                .into(holder.image);

        holder.btnIncrease.setOnClickListener(v -> {
            updateCartQuantity(item, "increase", holder.quantity);
        });

        holder.btnDecrease.setOnClickListener(v -> {
            if (item.getPurchaseQuantity() > 1) {
                updateCartQuantity(item, "decrease", holder.quantity);
            } else {
                Toast.makeText(context, "Min quantity is 1. Use delete instead.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView image, btnIncrease, btnDecrease;
        TextView name, price, quantity;

        CartViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.cart_product_image);
            name = itemView.findViewById(R.id.cart_product_name);
            price = itemView.findViewById(R.id.cart_product_price);
            quantity = itemView.findViewById(R.id.cart_product_quantity);
            btnIncrease = itemView.findViewById(R.id.cart_increase_btn);
            btnDecrease = itemView.findViewById(R.id.cart_decrease_btn);
        }
    }

    private void updateCartQuantity(CartItem item, String action, TextView quantityView) {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        Call<CartQtyUpdateResponse> call = apiService.updateCartQuantity(
                item.getUserId(),
                item.getProductId(),
                item.getCartId(),
                action
        );

        call.enqueue(new Callback<CartQtyUpdateResponse>() {
            @Override
            public void onResponse(Call<CartQtyUpdateResponse> call, Response<CartQtyUpdateResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String status = response.body().getStatus();
                    if ("success".equals(status)) {
                        int currentQty = item.getPurchaseQuantity();
                        int newQty = action.equals("increase") ? currentQty + 1 : currentQty - 1;
                        item.setPurchaseQuantity(newQty);
                        quantityView.setText(String.valueOf(newQty));

                        // ✅ Notify fragment to update total
                        if (cartUpdateListener != null) {
                            cartUpdateListener.onCartUpdated();
                        }

                    } else {
                        Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<CartQtyUpdateResponse> call, Throwable t) {
                Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
