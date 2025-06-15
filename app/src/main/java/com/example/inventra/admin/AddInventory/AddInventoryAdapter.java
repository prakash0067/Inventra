package com.example.inventra.admin.AddInventory;

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

public class AddInventoryAdapter extends RecyclerView.Adapter<AddInventoryAdapter.ProductViewHolder> {

    private List<Product2> productList;
    private OnProductClickListener listener;

    public interface OnProductClickListener {
        void onProductClick(Product2 product);
    }

    public AddInventoryAdapter(List<Product2> productList, OnProductClickListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_col, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product2 product = productList.get(position);
        holder.tvProductName.setText(product.getProductName());
        holder.tvProductPrice.setText("₹" + product.getPrice());
        holder.tvInStock.setText(String.valueOf(product.getQuantity()));
        holder.tvProductDesc.setText(product.getDescription());

        // Build full image URL
        String imageUrl = ApiClient.BASE_IMAGE_URL + product.getImage_path();

        // Load product image using Glide
        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.prod_default)
                .error(R.drawable.prod_default)
                .into(holder.imgProduct);

        holder.itemView.setOnClickListener(v -> {
            listener.onProductClick(product);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvProductPrice, tvInStock, tvProductDesc;
        ImageView imgProduct;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvInStock = itemView.findViewById(R.id.tvInStock);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvProductDesc = itemView.findViewById(R.id.tvProductDesc);
        }
    }
}
