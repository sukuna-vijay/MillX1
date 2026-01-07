package com.example.millx;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;

public class AdminMainActivity extends AppCompatActivity {

    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        RelativeLayout btnNotification = findViewById(R.id.btn_notification);
        if (btnNotification != null) {
            btnNotification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AdminMainActivity.this, AdminNotificationActivity.class);
                    startActivity(intent);
                }
            });
        }

        // Admin Dashboard Badge touch - always show menu page
        TextView btnAdminDashboard = findViewById(R.id.btn_admin_dashboard);
        if (btnAdminDashboard != null) {
            btnAdminDashboard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AdminMainActivity.this, AdminMenuActivity.class);
                    startActivity(intent);
                }
            });
        }

        MaterialCardView btnMachineDetails = findViewById(R.id.btn_machine_details);
        if (btnMachineDetails != null) {
            btnMachineDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Navigate to the new Admin Machine Management screen
                    Intent intent = new Intent(AdminMainActivity.this, AdminMachineManagementActivity.class);
                    startActivity(intent);
                }
            });
        }

        MaterialCardView btnPricesDetails = findViewById(R.id.btn_prices_details);
        if (btnPricesDetails != null) {
            btnPricesDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Navigate to the new Admin Price Management screen
                    Intent intent = new Intent(AdminMainActivity.this, AdminPriceManagementActivity.class);
                    startActivity(intent);
                }
            });
        }

        MaterialCardView btnStockDetails = findViewById(R.id.btn_stock_details);
        if (btnStockDetails != null) {
            btnStockDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Navigate to the new Admin Stock Management screen
                    Intent intent = new Intent(AdminMainActivity.this, AdminStockManagementActivity.class);
                    startActivity(intent);
                }
            });
        }

        // Customer Orders Button
        MaterialCardView btnCustomerOrders = findViewById(R.id.btn_customer_orders);
        if (btnCustomerOrders != null) {
            btnCustomerOrders.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AdminMainActivity.this, CustomerProductOrdersActivity.class);
                    startActivity(intent);
                }
            });
        }

        LinearLayout navHome = findViewById(R.id.nav_home);
        if (navHome != null) {
            navHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Already on Home
                }
            });
        }

        LinearLayout navMenu = findViewById(R.id.nav_menu);
        if (navMenu != null) {
            navMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AdminMainActivity.this, AdminMenuActivity.class);
                    startActivity(intent);
                }
            });
        }

        LinearLayout navProfile = findViewById(R.id.nav_profile);
        if (navProfile != null) {
            navProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AdminMainActivity.this, AdminProfileActivity.class);
                    startActivity(intent);
                }
            });
        }

        // Handle back press using the modern API (replacing deprecated onBackPressed)
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (doubleBackToExitPressedOnce) {
                    finishAffinity(); // Close the app
                    return;
                }

                doubleBackToExitPressedOnce = true;
                Toast.makeText(AdminMainActivity.this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            }
        });
    }
}
