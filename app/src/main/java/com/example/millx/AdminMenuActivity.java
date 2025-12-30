package com.example.millx;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;

public class AdminMenuActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu);

        MaterialCardView menuHome = findViewById(R.id.menu_home);
        if (menuHome != null) {
            menuHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        MaterialCardView menuMachine = findViewById(R.id.menu_machine);
        if (menuMachine != null) {
            menuMachine.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AdminMenuActivity.this, MachineStatusActivity.class);
                    startActivity(intent);
                }
            });
        }

        MaterialCardView menuStock = findViewById(R.id.menu_stock);
        if (menuStock != null) {
            menuStock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AdminMenuActivity.this, StockAvailableActivity.class);
                    startActivity(intent);
                }
            });
        }

        MaterialCardView menuPrice = findViewById(R.id.menu_price);
        if (menuPrice != null) {
            menuPrice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AdminMenuActivity.this, CurrentPricesActivity.class);
                    startActivity(intent);
                }
            });
        }

        MaterialCardView menuOrders = findViewById(R.id.menu_orders);
        if (menuOrders != null) {
            menuOrders.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AdminMenuActivity.this, CustomerProductOrdersActivity.class);
                    startActivity(intent);
                }
            });
        }

        MaterialCardView menuFeedback = findViewById(R.id.menu_feedback);
        if (menuFeedback != null) {
            menuFeedback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AdminMenuActivity.this, UserFeedbackActivity.class);
                    startActivity(intent);
                }
            });
        }

        MaterialCardView menuLogout = findViewById(R.id.menu_logout);
        if (menuLogout != null) {
            menuLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences sharedPreferences = getSharedPreferences("MillXPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();

                    Intent intent = new Intent(AdminMenuActivity.this, RoleSelectionActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }
}
