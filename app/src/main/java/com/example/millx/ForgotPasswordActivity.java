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
        com.google.android.material.textfield.TextInputEditText editEmail = findViewById(R.id.edit_email);

        if (btnSendCode != null) {
            btnSendCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String email = editEmail.getText().toString().trim();
                    if (email.isEmpty()) {
                        Toast.makeText(ForgotPasswordActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Toast.makeText(ForgotPasswordActivity.this, "Sending OTP...", Toast.LENGTH_SHORT).show();
                    
                    ApiService apiService = ApiClient.getClient().create(ApiService.class);
                    java.util.HashMap<String, String> body = new java.util.HashMap<>();
                    body.put("email", email);

                    apiService.forgotPassword(body).enqueue(new retrofit2.Callback<okhttp3.ResponseBody>() {
                        @Override
                        public void onResponse(retrofit2.Call<okhttp3.ResponseBody> call, retrofit2.Response<okhttp3.ResponseBody> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(ForgotPasswordActivity.this, "Verification code sent to your email", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ForgotPasswordActivity.this, VerifyCodeActivity.class);
                                intent.putExtra("role", role); 
                                intent.putExtra("email", email); 
                                startActivity(intent);
                            } else {
                                if (response.code() == 404) {
                                    Toast.makeText(ForgotPasswordActivity.this, "Email address not found", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ForgotPasswordActivity.this, "Server error: Mailer failed or internal error", Toast.LENGTH_LONG).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(retrofit2.Call<okhttp3.ResponseBody> call, Throwable t) {
                            Toast.makeText(ForgotPasswordActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
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
