package com.example.millx;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class MainActivity extends AppCompatActivity {

    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView btnNotification = findViewById(R.id.btn_notification);
        if (btnNotification != null) {
            btnNotification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
                    startActivity(intent);
                }
            });
        }

        LinearLayout navHome = findViewById(R.id.nav_home);
        if (navHome != null) {
            navHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Already on User Home
                }
            });
        }

        LinearLayout navMenu = findViewById(R.id.nav_menu);
        if (navMenu != null) {
            navMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                    startActivity(intent);
                }
            });
        }

        LinearLayout navProfile = findViewById(R.id.nav_profile);
        if (navProfile != null) {
            navProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(intent);
                }
            });
        }

        View btnMachineStatus = findViewById(R.id.btn_machine_status);
        if (btnMachineStatus != null) {
            btnMachineStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, MachineStatusActivity.class);
                    startActivity(intent);
                }
            });
        }

        View btnPrices = findViewById(R.id.btn_prices);
        if (btnPrices != null) {
            btnPrices.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, CurrentPricesActivity.class);
                    startActivity(intent);
                }
            });
        }

        View btnStockCheck = findViewById(R.id.btn_stock_check);
        if (btnStockCheck != null) {
            btnStockCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, StockAvailableActivity.class);
                    startActivity(intent);
                }
            });
        }

        // AI Quality Check Button
        MaterialCardView btnAiQualityCheck = findViewById(R.id.btn_ai_quality_check);
        if (btnAiQualityCheck != null) {
            btnAiQualityCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, AiQualityCheckActivity.class);
                    startActivity(intent);
                }
            });
        }

        // Book Order Button
        MaterialButton btnBookOrder = findViewById(R.id.btn_book_order);
        if (btnBookOrder != null) {
            btnBookOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, BookOrderActivity.class);
                    startActivity(intent);
                }
            });
        }

        // My Orders Button
        MaterialButton btnMyOrders = findViewById(R.id.btn_my_orders);
        if (btnMyOrders != null) {
            btnMyOrders.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, MyOrdersActivity.class);
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
                Toast.makeText(MainActivity.this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

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
