package com.example.millx;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserLoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnSignIn;
    private TextView textSignUp, textForgotPassword;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        sessionManager = new SessionManager(this);
        
        // Bind views
        etEmail = findViewById(R.id.edit_email);
        etPassword = findViewById(R.id.edit_password);
        btnSignIn = findViewById(R.id.btn_sign_in);
        textSignUp = findViewById(R.id.text_sign_up);
        textForgotPassword = findViewById(R.id.text_forgot_password);

        // Handle sign-in button click
        btnSignIn.setOnClickListener(v -> login());

        // Handle sign-up text click
        if (textSignUp != null) {
            textSignUp.setOnClickListener(v -> {
                Intent intent = new Intent(UserLoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            });
        }

        // Handle forgot password text click
        if (textForgotPassword != null) {
            textForgotPassword.setOnClickListener(v -> {
                Intent intent = new Intent(UserLoginActivity.this, ForgotPasswordActivity.class);
                intent.putExtra("role", "user");
                startActivity(intent);
            });
        }
    }

    private void login() {
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email and password required", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        LoginRequest request = new LoginRequest(email, password);

        apiService.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    
                    if ("success".equals(loginResponse.getStatus())) {
                        LoginResponse.UserData userData = loginResponse.getData();
                        
                        // Save session
                        sessionManager.setLogin(true, "user");
                        
                        String welcomeMsg = "Welcome";
                        if (userData != null && userData.getName() != null) {
                            welcomeMsg += " " + userData.getName();
                            // If you want to save specific user data
                            sessionManager.createLoginSession(userData.getId(), userData.getName(), "user");
                        }
                        
                        Toast.makeText(UserLoginActivity.this, welcomeMsg, Toast.LENGTH_SHORT).show();
                        
                        Intent intent = new Intent(UserLoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(UserLoginActivity.this, 
                            loginResponse.getMessage() != null ? loginResponse.getMessage() : "Login failed", 
                            Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UserLoginActivity.this, "Server error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(UserLoginActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
