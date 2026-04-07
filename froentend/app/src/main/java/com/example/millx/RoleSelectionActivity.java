package com.example.millx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;

public class RoleSelectionActivity extends AppCompatActivity {

    private MaterialCardView cardRegularUser;
    private MaterialCardView cardAdministrator;
    private LinearLayout btnHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_selection);

        // Initialize views
        cardRegularUser = findViewById(R.id.card_regular_user);
        cardAdministrator = findViewById(R.id.card_administrator);
        btnHelp = findViewById(R.id.btn_help);

        // Set click listeners
        cardRegularUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RoleSelectionActivity.this, UserLoginActivity.class);
                startActivity(intent);
            }
        });

        cardAdministrator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RoleSelectionActivity.this, AdminLoginActivity.class);
                startActivity(intent);
            }
        });

        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RoleSelectionActivity.this, "Help clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
