package com.example.millx;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.millx.AdminOrder;
import com.google.android.material.imageview.ShapeableImageView;
import java.util.List;

public class AdminOrderAdapter extends RecyclerView.Adapter<AdminOrderAdapter.ViewHolder> {

    private Context context;
    private List<AdminOrder> orderList;

    public AdminOrderAdapter(Context context, List<AdminOrder> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AdminOrder order = orderList.get(position);

        holder.userName.setText(order.getUserName());
        holder.userPhone.setText(order.getUserPhone());
        holder.orderId.setText("#ORD-" + order.getOrderId());
        holder.orderDate.setText(order.getDate());
        holder.productName.setText(order.getProductName());
        // Calculate total price
        try {
            double unitPrice = Double.parseDouble(order.getPrice());
            double total = unitPrice * order.getQuantity();
            holder.totalPrice.setText("₹" + String.format("%.2f", total));
            // Matching user side formatting exactly: Qty: 2 (₹25.00 / kg)
            holder.quantity.setText("Qty: " + order.getQuantity() + " (₹" + unitPrice + " / " + (order.getUnit() != null ? order.getUnit() : "unit") + ")");
        } catch (Exception e) {
            holder.totalPrice.setText("₹-");
            holder.quantity.setText("Qty: " + order.getQuantity());
        }

        // Status styling
        String status = order.getStatus();
        holder.status.setText(status);
        if ("confirmed".equalsIgnoreCase(status) || "completed".equalsIgnoreCase(status)) {
            holder.status.setTextColor(Color.parseColor("#16A34A"));
            holder.status.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#DCFCE7")));
        } else if ("pending".equalsIgnoreCase(status)) {
            holder.status.setTextColor(Color.parseColor("#D97706"));
            holder.status.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#FEF3C7")));
        } else {
            holder.status.setTextColor(Color.parseColor("#3182CE"));
            holder.status.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#EBF8FF")));
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
        return orderList != null ? orderList.size() : 0;
    }

    public void updateList(List<AdminOrder> newList) {
        if (newList != null) {
            this.orderList = newList;
        } else {
            this.orderList.clear();
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName, userPhone, orderId, orderDate, orderStatus, productName, quantity, status, totalPrice;
        ShapeableImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.tv_user_name);
            userPhone = itemView.findViewById(R.id.tv_user_phone);
            orderId = itemView.findViewById(R.id.tv_order_id);
            orderDate = itemView.findViewById(R.id.tv_order_date);
            productName = itemView.findViewById(R.id.tv_product_name);
            quantity = itemView.findViewById(R.id.tv_quantity);
            status = itemView.findViewById(R.id.tv_order_status);
            totalPrice = itemView.findViewById(R.id.tv_total_price);
            image = itemView.findViewById(R.id.img_product);
        }
    }
}
