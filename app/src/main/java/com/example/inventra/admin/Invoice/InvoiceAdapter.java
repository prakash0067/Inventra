package com.example.inventra.admin.Invoice;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.inventra.DBConnect.ApiClient;
import com.example.inventra.R;

import java.util.List;

// InvoiceAdapter.java
public class InvoiceAdapter extends RecyclerView.Adapter<InvoiceAdapter.ViewHolder> {
    List<ItemModel> items;

    public InvoiceAdapter(List<ItemModel> items) {
        this.items = items;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemDetails, itemPrice;
        ImageView itemImage;

        public ViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            itemDetails = itemView.findViewById(R.id.itemDetails);
            itemPrice = itemView.findViewById(R.id.itemPrice);
            itemImage = itemView.findViewById(R.id.itemImage);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_invoice, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ItemModel item = items.get(position);
        holder.itemName.setText(item.productName);
        holder.itemDetails.setText("Rate: ₹" + item.price + "\nQuantity: " + item.quantity);
        holder.itemPrice.setText("₹" + (item.price * item.quantity));
        // You can use Glide or Picasso to load image
        String imageUrl = ApiClient.BASE_IMAGE_URL + item.getImagePath();
        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.prod_default)
                .error(R.drawable.prod_default)
                .into(holder.itemImage);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
