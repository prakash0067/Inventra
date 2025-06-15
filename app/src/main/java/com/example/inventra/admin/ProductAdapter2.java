package com.example.inventra.admin;

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
import com.example.inventra.admin.SearchProducts.Product3;

import java.util.List;

public class ProductAdapter2 extends RecyclerView.Adapter<ProductAdapter2.ProductViewHolder> {

    private List<Product3> productList;
    private OnProductClickListener listener;

    public interface OnProductClickListener {
        void onProductClick(Product3 product);
    }

    public ProductAdapter2(List<Product3> productList, OnProductClickListener listener) {
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
        Product3 product = productList.get(position);

        holder.tvProductName.setText(product.getProductName());
        holder.tvProductPrice.setText("₹" + product.getPrice());
        holder.tvInStock.setText("Qty: " + product.getQuantity());

        // Load product image using Glide
        String imageUrl = ApiClient.BASE_IMAGE_URL + product.getImage_path();
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
        TextView tvProductName, tvProductPrice, tvInStock;
        ImageView imgProduct;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvInStock = itemView.findViewById(R.id.tvInStock);
        }
    }
}