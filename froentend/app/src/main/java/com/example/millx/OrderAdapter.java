package com.example.millx;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.imageview.ShapeableImageView;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    public interface OnOrderCancelListener {
        void onCancelClick(Order order);
    }

    private Context context;
    private List<Order> orderList;
    private OnOrderCancelListener cancelListener;

    public OrderAdapter(Context context, List<Order> orderList, OnOrderCancelListener cancelListener) {
        this.context = context;
        this.orderList = orderList;
        this.cancelListener = cancelListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.orderId.setText("Order #" + order.getOrderId());
        holder.productName.setText(order.getProductName());
        holder.quantity.setText("Qty: " + order.getQuantity());
        holder.date.setText(order.getDate());

        // Status formatting
        String status = order.getStatus();
        holder.status.setText(status);
        if ("confirmed".equalsIgnoreCase(status)) {
            holder.status.setTextColor(Color.parseColor("#38A169"));
            holder.status.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#F0FFF4")));
            holder.btnCancel.setVisibility(View.VISIBLE);
        } else if ("processing".equalsIgnoreCase(status)) {
            holder.status.setTextColor(Color.parseColor("#3182CE"));
            holder.status.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#EBF8FF")));
            holder.btnCancel.setVisibility(View.VISIBLE);
        } else {
            holder.status.setTextColor(Color.parseColor("#718096"));
            holder.status.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#F7FAFC")));
            holder.btnCancel.setVisibility(View.GONE);
        }

        // Cancel button click
        holder.btnCancel.setOnClickListener(v -> {
            if (cancelListener != null) {
                cancelListener.onCancelClick(order);
            }
        });

        // Calculate total price
        try {
            double unitPrice = Double.parseDouble(order.getPrice());
            double total = unitPrice * order.getQuantity();
            holder.totalPrice.setText("₹" + String.format("%.2f", total));
            holder.quantity.setText("Qty: " + order.getQuantity() + " (₹" + unitPrice + " / " + (order.getUnit() != null ? order.getUnit() : "unit") + ")");
        } catch (Exception e) {
            holder.totalPrice.setText("₹-");
        }

        // Image
        String imageUrl = ApiClient.BASE_URL + order.getImage();
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.logo)
                .error(R.drawable.logo)
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderId, status, date, productName, quantity, totalPrice;
        ShapeableImageView image;
        com.google.android.material.button.MaterialButton btnCancel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.tv_order_id);
            status = itemView.findViewById(R.id.tv_order_status);
            date = itemView.findViewById(R.id.tv_order_date);
            productName = itemView.findViewById(R.id.tv_product_name);
            quantity = itemView.findViewById(R.id.tv_quantity);
            totalPrice = itemView.findViewById(R.id.tv_total_price);
            image = itemView.findViewById(R.id.img_order_product);
            btnCancel = itemView.findViewById(R.id.btn_cancel_order);
        }
    }
}
