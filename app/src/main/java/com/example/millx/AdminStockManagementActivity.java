package com.example.millx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AdminStockManagementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_stock_management);

        // Using View type to avoid ClassCastException as btn_back is a MaterialCardView in XML
        View btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        FloatingActionButton fabAddStock = findViewById(R.id.fab_add_stock);
        if (fabAddStock != null) {
            fabAddStock.setOnClickListener(v -> {
                Intent intent = new Intent(AdminStockManagementActivity.this, AddStockItemActivity.class);
                startActivity(intent);
            });
        }

        // Set up edit buttons
        setupEditButtons();
    }

    private void setupEditButtons() {
        int[] editButtonIds = {
                R.id.btn_edit1, R.id.btn_edit2, R.id.btn_edit3, R.id.btn_edit4
        };

        for (int id : editButtonIds) {
            View btnEdit = findViewById(id);
            if (btnEdit != null) {
                btnEdit.setOnClickListener(v -> {
                    Intent intent = new Intent(AdminStockManagementActivity.this, EditStockItemActivity.class);
                    startActivity(intent);
                });
            }
        }
    }
}
