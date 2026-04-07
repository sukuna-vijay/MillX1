package com.example.millx;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.imageview.ShapeableImageView;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private Context context;
    private List<Product> productList;
    private Map<Integer, Integer> quantities = new HashMap<>();

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_book_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.name.setText(product.getName());
        holder.price.setText("₹" + product.getPrice() + " / " + product.getUnit());

        // Image loading
        String imageUrl = ApiClient.BASE_URL + product.getImage();
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.logo)
                .error(R.drawable.logo)
                .into(holder.image);

        // Quantity logic
        int qty = quantities.getOrDefault(product.getId(), 0);
        holder.quantity.setText(String.valueOf(qty));

        holder.btnPlus.setOnClickListener(v -> {
            int currentQty = quantities.getOrDefault(product.getId(), 0);
            quantities.put(product.getId(), currentQty + 1);
            notifyItemChanged(position);
        });

        holder.btnMinus.setOnClickListener(v -> {
            int currentQty = quantities.getOrDefault(product.getId(), 0);
            if (currentQty > 0) {
                quantities.put(product.getId(), currentQty - 1);
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public Map<Integer, Integer> getSelectedQuantities() {
        return quantities;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, stock, quantity;
        ShapeableImageView image;
        ImageView btnPlus, btnMinus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_product_name);
            price = itemView.findViewById(R.id.tv_product_price);
            stock = itemView.findViewById(R.id.tv_product_stock);
            quantity = itemView.findViewById(R.id.tv_quantity);
            image = itemView.findViewById(R.id.img_product);
            btnPlus = itemView.findViewById(R.id.btn_plus);
            btnMinus = itemView.findViewById(R.id.btn_minus);
        }
    }
}
