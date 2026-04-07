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
                loginAdmin();
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

        // Handle back press using the modern API
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

    private void loginAdmin() {
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        // Pass "admin" as role, though the backend might just verify credentials
        // against 'users'
        // The third parameter in LoginRequest constructor is likely used for backend
        // role check if implemented
        LoginRequest request = new LoginRequest(email, password, "admin");

        apiService.login(request).enqueue(new retrofit2.Callback<LoginResponse>() {
            @Override
            public void onResponse(retrofit2.Call<LoginResponse> call, retrofit2.Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    if ("success".equals(loginResponse.getStatus())) {
                        LoginResponse.UserData userData = loginResponse.getData();

                        // Strict Role Check
                        if (userData != null && "admin".equalsIgnoreCase(userData.getRole())) {
                            sessionManager.setLogin(true, "admin");
                            sessionManager.createLoginSession(
                                    userData.getId(),
                                    userData.getName(),
                                    "admin",
                                    userData.getPhone() != null ? userData.getPhone() : "",
                                    userData.getProfileImage() != null ? userData.getProfileImage() : "",
                                    loginResponse.getToken());

                            Toast.makeText(AdminLoginActivity.this, "Welcome " + userData.getName(), Toast.LENGTH_SHORT)
                                    .show();

                            Intent intent = new Intent(AdminLoginActivity.this, AdminMainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(AdminLoginActivity.this, "Access Denied: Not an Admin", Toast.LENGTH_LONG)
                                    .show();
                        }
                    } else {
                        Toast.makeText(AdminLoginActivity.this, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AdminLoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<LoginResponse> call, Throwable t) {
                Toast.makeText(AdminLoginActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
