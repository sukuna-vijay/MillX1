package com.example.millx;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class MenuActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        MaterialCardView menuHome = findViewById(R.id.menu_home);
        if (menuHome != null) {
            menuHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        MaterialCardView menuProfile = findViewById(R.id.menu_profile);
        if (menuProfile != null) {
            menuProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MenuActivity.this, ProfileActivity.class);
                    startActivity(intent);
                }
            });
        }

        MaterialCardView menuFeedback = findViewById(R.id.menu_feedback);
        if (menuFeedback != null) {
            menuFeedback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showFeedbackDialog();
                }
            });
        }

        MaterialCardView menuLogout = findViewById(R.id.btn_logout); // Use btn_logout to match the ID in XML
        if (menuLogout != null) {
            menuLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Clear session and logout
                    SharedPreferences sharedPreferences = getSharedPreferences("MillXPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();

                    Intent intent = new Intent(MenuActivity.this, RoleSelectionActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    private void showFeedbackDialog() {
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_feedback);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        ImageView btnClose = dialog.findViewById(R.id.btn_close);
        MaterialButton btnLater = dialog.findViewById(R.id.btn_later);
        MaterialButton btnSubmit = dialog.findViewById(R.id.btn_submit);

        if (btnClose != null) {
            btnClose.setOnClickListener(v -> dialog.dismiss());
        }
        if (btnLater != null) {
            btnLater.setOnClickListener(v -> dialog.dismiss());
        }
        if (btnSubmit != null) {
            btnSubmit.setOnClickListener(v -> {
                Toast.makeText(this, "Feedback submitted. Thank you!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });
        }

        dialog.show();
    }
}
