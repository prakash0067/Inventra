package com.example.inventra.admin;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inventra.InvoiceDetailActivity;
import com.example.inventra.R;

import java.util.List;

public class BillingAdapter extends RecyclerView.Adapter<BillingAdapter.BillingViewHolder> {

    List<BillingModel> billingList;

    public BillingAdapter(List<BillingModel> billingList) {
        this.billingList = billingList;
    }

    @NonNull
    @Override
    public BillingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.billing_item, parent, false);
        return new BillingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillingViewHolder holder, int position) {
        BillingModel model = billingList.get(position);

        holder.billNumber.setText("Bill #" + model.getBillNumber());
        holder.customerName.setText("Customer: " + model.getCustomerName());
        holder.saleDate.setText("Date: " + model.getSaleDate());
        holder.saleTime.setText("Time: " + model.getSalesTime());
        holder.totalAmount.setText(model.getTotalAmount());

        View expandToggle = holder.itemView.findViewById(R.id.expandToggle);
        expandToggle.setOnClickListener(v -> {
            Intent intent = new Intent(holder.context, InvoiceDetailActivity.class);
            intent.putExtra("sale_id", model.getBillNumber()); // Pass sale_id
            holder.context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return billingList.size();
    }

    static class BillingViewHolder extends RecyclerView.ViewHolder {
        TextView billNumber, customerName, saleDate, totalAmount, saleTime;
        Context context;
        public BillingViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext(); // store context
            billNumber = itemView.findViewById(R.id.billNumber);
            customerName = itemView.findViewById(R.id.customerName);
            saleDate = itemView.findViewById(R.id.saleDate);
            totalAmount = itemView.findViewById(R.id.totalAmount);
            saleTime = itemView.findViewById(R.id.saleTime);
        }
    }
}
