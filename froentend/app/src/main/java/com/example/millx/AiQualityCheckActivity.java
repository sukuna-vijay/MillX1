package com.example.millx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class AiQualityCheckActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_quality_check);

        ImageView btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Set Click Listeners for all items
        setupItemClick(R.id.item_chilli, "Chilli Powder", R.drawable.ic_chilli_rate, "CP-001");
        setupItemClick(R.id.item_rice, "Rice", R.drawable.ic_rice_rate, "RI-002");
        setupItemClick(R.id.item_parboiled, "Tumeric Powder", R.drawable.ic_wheat_machine, "PR-003");
        setupItemClick(R.id.item_bran, "Rice Bran", R.drawable.ic_karuka_machine, "RB-004");
        setupItemClick(R.id.item_flour, "Rice Flour", R.drawable.ic_flour_machine, "RF-005");

    }

    private void setupItemClick(int id, String name, int imageRes, String productId) {
        View view = findViewById(id);
        if (view != null) {
            view.setOnClickListener(v -> {
                Intent intent = new Intent(AiQualityCheckActivity.this, QualityReportActivity.class);
                intent.putExtra("product_name", name);
                intent.putExtra("product_image", imageRes);
                intent.putExtra("product_id", productId);
                startActivity(intent);
            });
        }
    }
}
