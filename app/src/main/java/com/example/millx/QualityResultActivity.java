package com.example.millx;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class QualityResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quality_result);

        ImageView btnBack = findViewById(R.id.btn_back);
        ImageView ivResultImage = findViewById(R.id.iv_result_image);
        TextView tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        MaterialButton btnBackHome = findViewById(R.id.btn_back_home);

        // Get data from intent
        String name = getIntent().getStringExtra("product_name");
        byte[] byteArray = getIntent().getByteArrayExtra("captured_image");

        if (name != null) {
            tvToolbarTitle.setText(name + " Result");
        }

        if (byteArray != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            ivResultImage.setImageBitmap(bitmap);
        } else {
            int imageRes = getIntent().getIntExtra("product_image", R.drawable.ic_rice_rate);
            ivResultImage.setImageResource(imageRes);
        }

        btnBack.setOnClickListener(v -> finish());

        btnBackHome.setOnClickListener(v -> {
            Intent intent = new Intent(QualityResultActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }
}
