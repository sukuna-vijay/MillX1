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
import com.example.millx.R;

import java.util.List;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.ViewHolder> {

    private Context context;
    private List<Stock> stockList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Stock stock);
    }

    public StockAdapter(Context context, List<Stock> stockList, OnItemClickListener listener) {
        this.context = context;
        this.stockList = stockList;
        this.listener = listener;
    }

    public void updateList(List<Stock> newList) {
        this.stockList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_stock, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Stock stock = stockList.get(position);
        holder.tvName.setText(stock.getName());
        holder.tvQuantity.setText(String.valueOf(stock.getQuantity()));
        holder.tvUnit.setText(stock.getUnit());

        if (stock.getImage() != null && !stock.getImage().isEmpty()) {
            String fullUrl = ApiClient.BASE_URL + stock.getImage().replace("../", "");
            Glide.with(context)
                    .load(fullUrl)
                    .placeholder(R.drawable.ic_wheat_rate) // Fallback
                    .error(R.drawable.ic_wheat_rate)
                    .into(holder.ivImage);
        } else {
            holder.ivImage.setImageResource(R.drawable.ic_wheat_rate);
        }

        if (listener != null) {
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnEdit.setOnClickListener(v -> listener.onItemClick(stock));
        } else {
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnEdit.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName, tvQuantity, tvUnit;
        View btnEdit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_stock_image);
            tvName = itemView.findViewById(R.id.tv_stock_name);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvUnit = itemView.findViewById(R.id.tv_unit);
            btnEdit = itemView.findViewById(R.id.btn_edit_stock);
        }
    }
}
