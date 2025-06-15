package com.example.inventra.admin.CartDetails;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.inventra.DBConnect.ApiClient;
import com.example.inventra.R;

import java.util.List;

// class to use in order summary
public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartViewHolder> {

    private List<CartItem> cartList;
    private Context context;

    public CartItemAdapter(Context context, List<CartItem> cartList) {
        this.context = context;
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_invoice, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartList.get(position);

        holder.itemName.setText(item.getProductName());
        holder.itemDetails.setText("Rate: ₹" + item.getPrice() + "\nQuantity: " + item.getPurchaseQuantity());
        int total = item.getPrice() * item.getPurchaseQuantity();
        holder.itemPrice.setText("₹" + total);

        // Load image (if using Glide/Picasso)
        String imageUrl = ApiClient.BASE_IMAGE_URL + item.getProductImage();
        Glide.with(context)
                .load(imageUrl) // assuming full URL
                .placeholder(R.drawable.baseline_image_search_24)
                .into(holder.itemImage);
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemDetails, itemPrice;
        ImageView itemImage;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            itemDetails = itemView.findViewById(R.id.itemDetails);
            itemPrice = itemView.findViewById(R.id.itemPrice);
            itemImage = itemView.findViewById(R.id.itemImage);
        }
    }
}
