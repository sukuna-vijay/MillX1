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
}
