package com.example.inventra;

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
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class StaffAdapter extends RecyclerView.Adapter<StaffAdapter.StaffViewHolder> {

    private List<Staff> staffList;
    private Context context;
    private OnDeleteListener onDeleteListener;

    public StaffAdapter(List<Staff> staffList, Context context, OnDeleteListener onDeleteListener) {
        this.staffList = staffList;
        this.context = context;
        this.onDeleteListener = onDeleteListener;
    }

    @NonNull
    @Override
    public StaffViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.staff_card_item, parent, false);
        return new StaffViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StaffViewHolder holder, int position) {
        Staff staff = staffList.get(position);
        holder.name.setText(staff.getName());
        holder.email.setText(staff.getEmail());
        holder.create_date.setText(staff.getCreated_at());

        // Build full image URL
        String imageUrl = ApiClient.BASE_PROFILE_URL + staff.getProfile_pic();

        // Load product image from URL using Glide
        Glide.with(holder.itemView.getContext())
                .load(imageUrl) // Make sure this returns a valid image URL
                .placeholder(R.drawable.prod_default) // Optional: show while loading
                .error(R.drawable.prod_default)
                .into(holder.profilePic);

        // Do not handle delete here – it's handled by swipe
    }

    @Override
    public int getItemCount() {
        return staffList.size();
    }

    public void removeItem(int position) {
        if (position >= 0 && position < staffList.size()) {
            staffList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Staff getItem(int position) {
        return staffList.get(position);
    }

    public static class StaffViewHolder extends RecyclerView.ViewHolder {
        TextView name, email, create_date;
        View foregroundView, backgroundView;
        ShapeableImageView profilePic;

        public StaffViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.staffNameText);
            email = itemView.findViewById(R.id.staffEmailText);
            foregroundView = itemView.findViewById(R.id.foreground_view);
            backgroundView = itemView.findViewById(R.id.background_view);
            profilePic = itemView.findViewById(R.id.avatarImage);
            create_date = itemView.findViewById(R.id.staffCreatedText);
        }
    }

    public interface OnDeleteListener {
        void onDelete(int staffId, int position);
    }
}