package com.example.inventra.admin.RecentInvoice;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inventra.InvoiceDetailActivity;
import com.example.inventra.R;

import java.util.List;

public class RecentInvoiceAdapter extends RecyclerView.Adapter<RecentInvoiceAdapter.InvoiceViewHolder> {

    private Context context;
    private List<RecentInvoice> invoices;

    public RecentInvoiceAdapter(Context context, List<RecentInvoice> invoices) {
        this.context = context;
        this.invoices = invoices;
    }

    @NonNull
    @Override
    public InvoiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recent_invoice, parent, false);
        return new InvoiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvoiceViewHolder holder, int position) {
        RecentInvoice invoice = invoices.get(position);

        holder.customerName.setText(invoice.getCustomerName());
        holder.invoiceDateTime.setText(invoice.getDateTime());
        holder.totalAmount.setText("₹ " + invoice.getTotalAmount());
        holder.statusBadge.setText(invoice.getStatus().toUpperCase());

        // Randomly pick one of 5 icons
        int[] icons = {
                R.drawable.bill_ticket_1,
                R.drawable.bill_ticket_2,
                R.drawable.bill_ticket_3,
                R.drawable.bill_ticket_4,
                R.drawable.bill_ticket_5
        };

        int randomIndex = (int) (Math.random() * icons.length);
        holder.invoiceIcon.setImageResource(icons[randomIndex]);

        int index = position % icons.length;
        holder.invoiceIcon.setImageResource(icons[index]);


        if (invoice.getStatus().equalsIgnoreCase("Paid")) {
            holder.statusBadge.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.green));
        } else {
            holder.statusBadge.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.orange));
        }

        // Handle item click
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, InvoiceDetailActivity.class);
            intent.putExtra("sale_id", invoice.getSalesId());
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return invoices.size();
    }

    static class InvoiceViewHolder extends RecyclerView.ViewHolder {
        TextView customerName, invoiceDateTime, totalAmount, statusBadge;
        ImageView invoiceIcon;

        InvoiceViewHolder(View itemView) {
            super(itemView);
            customerName = itemView.findViewById(R.id.customerName);
            invoiceDateTime = itemView.findViewById(R.id.invoiceDateTime);
            totalAmount = itemView.findViewById(R.id.totalAmount);
            statusBadge = itemView.findViewById(R.id.statusBadge);
            invoiceIcon = itemView.findViewById(R.id.invoiceIcon);
        }
    }
}
