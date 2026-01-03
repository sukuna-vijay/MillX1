package com.example.millx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AdminPriceManagementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_price_management);

        ImageView btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        FloatingActionButton fabAddPrice = findViewById(R.id.fab_add_price);
        if (fabAddPrice != null) {
            fabAddPrice.setOnClickListener(v -> {
                Intent intent = new Intent(AdminPriceManagementActivity.this, AddNewProductActivity.class);
                startActivity(intent);
            });
        }
    }
}
