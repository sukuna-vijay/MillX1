package com.example.millx;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            SharedPreferences sharedPreferences = getSharedPreferences("MillXPrefs", MODE_PRIVATE);
            boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
            String role = sharedPreferences.getString("role", "");

            Intent intent;
            if (isLoggedIn) {
                if ("admin".equals(role)) {
                    intent = new Intent(SplashActivity.this, AdminMainActivity.class);
                } else {
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                }
            } else {
                intent = new Intent(SplashActivity.this, RoleSelectionActivity.class);
            }
            startActivity(intent);
            finish();
        }, 2000); // 2 seconds delay
    }
}
