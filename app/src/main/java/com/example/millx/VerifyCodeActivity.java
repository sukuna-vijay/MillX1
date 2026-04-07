package com.example.millx;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import java.util.Locale;

public class VerifyCodeActivity extends AppCompatActivity {

    private TextView textResend;
    private CountDownTimer countDownTimer;
    private String role;
    private String email;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_code);

        role = getIntent().getStringExtra("role");
        email = getIntent().getStringExtra("email");
        type = getIntent().getStringExtra("type");

        if (type == null) {
            type = "forgot_password"; // default to forgot password for backward compatibility
        }

        MaterialButton btnVerify = findViewById(R.id.btn_verify);
        textResend = findViewById(R.id.text_resend);
        
        android.widget.EditText code1 = findViewById(R.id.code1);
        android.widget.EditText code2 = findViewById(R.id.code2);
        android.widget.EditText code3 = findViewById(R.id.code3);
        android.widget.EditText code4 = findViewById(R.id.code4);

        // Auto move to next box logic
        setupOtpAutoMove(code1, code2);
        setupOtpAutoMove(code2, code3);
        setupOtpAutoMove(code3, code4);
        setupOtpAutoMove(code4, null);

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = code1.getText().toString() + code2.getText().toString() + code3.getText().toString() + code4.getText().toString();
                if (otp.length() < 4) {
                    android.widget.Toast.makeText(VerifyCodeActivity.this, "Please enter 4-digit code", android.widget.Toast.LENGTH_SHORT).show();
                    return;
                }

                android.widget.Toast.makeText(VerifyCodeActivity.this, "Verifying...", android.widget.Toast.LENGTH_SHORT).show();

                ApiService apiService = ApiClient.getClient().create(ApiService.class);
                java.util.HashMap<String, String> body = new java.util.HashMap<>();
                body.put("email", email);
                body.put("otp", otp);

                retrofit2.Callback<okhttp3.ResponseBody> callback = new retrofit2.Callback<okhttp3.ResponseBody>() {
                    @Override
                    public void onResponse(retrofit2.Call<okhttp3.ResponseBody> call, retrofit2.Response<okhttp3.ResponseBody> response) {
                        if (response.isSuccessful()) {
                            android.widget.Toast.makeText(VerifyCodeActivity.this, "Code Verified!", android.widget.Toast.LENGTH_SHORT).show();
                            
                            if ("signup".equals(type)) {
                                Intent intent = new Intent(VerifyCodeActivity.this, UserLoginActivity.class);
                                intent.putExtra("role", role);
                                startActivity(intent);
                                finishAffinity(); // clear stack and go to login
                            } else {
                                Intent intent = new Intent(VerifyCodeActivity.this, ResetPasswordActivity.class);
                                intent.putExtra("role", role);
                                intent.putExtra("email", email);
                                intent.putExtra("otp", otp);
                                startActivity(intent);
                            }
                        } else {
                            android.widget.Toast.makeText(VerifyCodeActivity.this, "Invalid or expired code", android.widget.Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<okhttp3.ResponseBody> call, Throwable t) {
                        android.widget.Toast.makeText(VerifyCodeActivity.this, "Network error: " + t.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                    }
                };

                if ("signup".equals(type)) {
                    apiService.verifySignupOtp(body).enqueue(callback);
                } else {
                    apiService.verifyOtp(body).enqueue(callback);
                }
            }
        });

        startTimer();
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int minutes = (int) (millisUntilFinished / 1000) / 60;
                int seconds = (int) (millisUntilFinished / 1000) % 60;
                String timeLeft = String.format(Locale.getDefault(), "Resend in %d:%02d", minutes, seconds);
                textResend.setText(timeLeft);
            }

            @Override
            public void onFinish() {
                textResend.setText("Resend now");
                textResend.setOnClickListener(v -> {
                    startTimer();
                });
            }
        }.start();
    }

    private void setupOtpAutoMove(android.widget.EditText current, android.widget.EditText next) {
        current.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1 && next != null) {
                    next.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        // Add backspace logic
        current.setOnKeyListener(new android.view.View.OnKeyListener() {
            @Override
            public boolean onKey(android.view.View v, int keyCode, android.view.KeyEvent event) {
                if (keyCode == android.view.KeyEvent.KEYCODE_DEL && 
                    event.getAction() == android.view.KeyEvent.ACTION_DOWN && 
                    current.getText().length() == 0) {
                    
                    // Move to previous view if it's an EditText
                    android.view.View prev = v.focusSearch(android.view.View.FOCUS_LEFT);
                    if (prev instanceof android.widget.EditText) {
                        prev.requestFocus();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
