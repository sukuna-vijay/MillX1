package com.example.millx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class AdminLoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        sessionManager = new SessionManager(this);
        
        etEmail = findViewById(R.id.edit_admin_email);
        etPassword = findViewById(R.id.edit_admin_password);
        MaterialButton btnSignIn = findViewById(R.id.btn_sign_in);
        TextView textForgotPassword = findViewById(R.id.text_forgot_password);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
                String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

                // Hardcoded Admin check
                if (email.equals("vijayjk182005@gmail.com") && password.equals("123456")) {
                    // Save admin login state
                    sessionManager.setLogin(true, "admin");
                    sessionManager.createLoginSession(1, "Vijay JK", "admin");

                    Toast.makeText(AdminLoginActivity.this, "Welcome Admin", Toast.LENGTH_SHORT).show();

                    // Successful Admin Login Navigation
                    Intent intent = new Intent(AdminLoginActivity.this, AdminMainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(AdminLoginActivity.this, "Access Denied: Invalid Admin Credentials", Toast.LENGTH_LONG).show();
                }
            }
        });

        textForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminLoginActivity.this, ForgotPasswordActivity.class);
                intent.putExtra("role", "admin");
                startActivity(intent);
            }
        });

        // Handle back press using the modern API (replacing deprecated onBackPressed)
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(AdminLoginActivity.this, RoleSelectionActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });
    }
}
