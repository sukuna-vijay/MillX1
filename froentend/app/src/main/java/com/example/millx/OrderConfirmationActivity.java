package com.example.millx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class OrderConfirmationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);
 
        android.widget.LinearLayout itemContainer = findViewById(R.id.item_list_container);
        android.widget.TextView tvTotalCount = findViewById(R.id.tv_total_items_count);
 
        java.util.ArrayList<String> orderItems = getIntent().getStringArrayListExtra("order_items");
        int totalCount = getIntent().getIntExtra("total_count", 0);
 
        if (tvTotalCount != null) {
            tvTotalCount.setText(String.valueOf(totalCount));
        }
 
        if (itemContainer != null && orderItems != null) {
            for (String item : orderItems) {
                addItemToSummary(itemContainer, item);
            }
        }
 
        MaterialButton btnViewOrders = findViewById(R.id.btn_view_orders);
        if (btnViewOrders != null) {
            btnViewOrders.setOnClickListener(v -> {
                Intent intent = new Intent(OrderConfirmationActivity.this, MyOrdersActivity.class);
                startActivity(intent);
                finish();
            });
        }
 
        MaterialButton btnContinue = findViewById(R.id.btn_continue);
        if (btnContinue != null) {
            btnContinue.setOnClickListener(v -> {
                Intent intent = new Intent(OrderConfirmationActivity.this, BookOrderActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            });
        }
    }
 
    private void addItemToSummary(android.widget.LinearLayout container, String itemText) {
        android.widget.RelativeLayout relativeLayout = new android.widget.RelativeLayout(this);
        android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, (int) (12 * getResources().getDisplayMetrics().density));
        relativeLayout.setLayoutParams(params);
 
        String[] parts = itemText.split(" x ");
        String name = parts[0];
        String qty = "x " + (parts.length > 1 ? parts[1] : "1");
 
        android.widget.TextView tvName = new android.widget.TextView(this);
        tvName.setText(name);
        tvName.setTextColor(android.graphics.Color.parseColor("#1A1C1E"));
        tvName.setTextSize(15);
        tvName.setTypeface(null, android.graphics.Typeface.BOLD);
 
        android.widget.TextView tvQty = new android.widget.TextView(this);
        tvQty.setText(qty);
        tvQty.setTextColor(android.graphics.Color.parseColor("#74777F"));
        tvQty.setTextSize(15);
        android.widget.RelativeLayout.LayoutParams qtyParams = new android.widget.RelativeLayout.LayoutParams(
                android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT,
                android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT);
        qtyParams.addRule(android.widget.RelativeLayout.ALIGN_PARENT_END);
        tvQty.setLayoutParams(qtyParams);
 
        relativeLayout.addView(tvName);
        relativeLayout.addView(tvQty);
        container.addView(relativeLayout);
    }
}
