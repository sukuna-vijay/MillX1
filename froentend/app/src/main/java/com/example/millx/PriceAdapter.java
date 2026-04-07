package com.example.millx;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class PriceAdapter extends RecyclerView.Adapter<PriceAdapter.PriceViewHolder> {

    private Context context;
    private List<Product> productList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    public PriceAdapter(Context context, List<Product> productList, OnItemClickListener listener) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
    }

    public void updateList(List<Product> list) {
        this.productList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PriceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new PriceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PriceViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.tvName.setText(product.getName());
        holder.tvDesc.setText(product.getDescription());
        holder.tvPrice.setText("Rs " + String.valueOf(product.getPrice()));
        holder.tvUnit.setText("per " + product.getUnit());

        // Image Loading
        String imagePath = product.getImage();
        if (imagePath != null && !imagePath.isEmpty()) {
            String fullUrl = ApiClient.BASE_URL + imagePath.replace("../", "");
            Glide.with(context)
                    .load(fullUrl)
                    .placeholder(R.drawable.ic_wheat_rate) // Default as fallback
                    .error(R.drawable.ic_wheat_rate)
                    .into(holder.ivImage);
        } else {
            // Mapping for default static images based on name (Optional)
            // For now use default
            holder.ivImage.setImageResource(R.drawable.ic_wheat_rate);
        }

        if (listener != null) {
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnEdit.setOnClickListener(v -> listener.onItemClick(product));
        } else {
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnEdit.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class PriceViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDesc, tvPrice, tvUnit;
        ImageView ivImage;
        View btnEdit;

        public PriceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_product_name);
            tvDesc = itemView.findViewById(R.id.tv_product_desc);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvUnit = itemView.findViewById(R.id.tv_unit);
            ivImage = itemView.findViewById(R.id.iv_product_image);
            btnEdit = itemView.findViewById(R.id.btn_edit_product);
        }
    }
}
