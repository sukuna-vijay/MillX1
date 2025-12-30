package com.example.millx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class BookOrderActivity extends AppCompatActivity {

    private int q1 = 1, q2 = 0, q3 = 2, q4 = 0;
    private TextView tvQ1, tvQ2, tvQ3, tvQ4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_order);

        tvQ1 = findViewById(R.id.tv_quantity_1);
        tvQ2 = findViewById(R.id.tv_quantity_2);
        tvQ3 = findViewById(R.id.tv_quantity_3);
        tvQ4 = findViewById(R.id.tv_quantity_4);

        setupQuantityControls();

        MaterialCardView btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        MaterialButton btnBookNow = findViewById(R.id.btn_book_now);
        if (btnBookNow != null) {
            btnBookNow.setOnClickListener(v -> {
                Intent intent = new Intent(BookOrderActivity.this, OrderConfirmationActivity.class);
                startActivity(intent);
                finish();
            });
        }
    }

    private void setupQuantityControls() {
        // Item 1
        findViewById(R.id.btn_plus_1).setOnClickListener(v -> {
            q1++;
            tvQ1.setText(String.valueOf(q1));
        });
        findViewById(R.id.btn_minus_1).setOnClickListener(v -> {
            if (q1 > 0) {
                q1--;
                tvQ1.setText(String.valueOf(q1));
            }
        });

        // Item 2
        findViewById(R.id.btn_plus_2).setOnClickListener(v -> {
            q2++;
            tvQ2.setText(String.valueOf(q2));
        });
        findViewById(R.id.btn_minus_2).setOnClickListener(v -> {
            if (q2 > 0) {
                q2--;
                tvQ2.setText(String.valueOf(q2));
            }
        });

        // Item 3
        findViewById(R.id.btn_plus_3).setOnClickListener(v -> {
            q3++;
            tvQ3.setText(String.valueOf(q3));
        });
        findViewById(R.id.btn_minus_3).setOnClickListener(v -> {
            if (q3 > 0) {
                q3--;
                tvQ3.setText(String.valueOf(q3));
            }
        });

        // Item 4
        findViewById(R.id.btn_plus_4).setOnClickListener(v -> {
            q4++;
            tvQ4.setText(String.valueOf(q4));
        });
        findViewById(R.id.btn_minus_4).setOnClickListener(v -> {
            if (q4 > 0) {
                q4--;
                tvQ4.setText(String.valueOf(q4));
            }
        });
    }
}
