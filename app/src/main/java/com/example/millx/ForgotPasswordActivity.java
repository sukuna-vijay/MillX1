package com.example.millx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class ForgotPasswordActivity extends AppCompatActivity {

    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        role = getIntent().getStringExtra("role");

        MaterialButton btnSendCode = findViewById(R.id.btn_send_code);
        TextView textBackToLogin = findViewById(R.id.text_back_to_login);

        if (btnSendCode != null) {
            btnSendCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(ForgotPasswordActivity.this, "Verification code sent to your email", Toast.LENGTH_SHORT).show();
                    
                    Intent intent = new Intent(ForgotPasswordActivity.this, VerifyCodeActivity.class);
                    intent.putExtra("role", role); // Pass the role through
                    startActivity(intent);
                }
            });
        }

        if (textBackToLogin != null) {
            textBackToLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }
}
