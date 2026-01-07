package com.example.millx;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    TextInputEditText etName, etEmail, etPassword, etConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etName = findViewById(R.id.edit_name);
        etEmail = findViewById(R.id.edit_email);
        etPassword = findViewById(R.id.edit_password);
        etConfirmPassword = findViewById(R.id.edit_confirm_password);

        MaterialButton btnSignUp = findViewById(R.id.btn_sign_up);
        btnSignUp.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {

        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirm = etConfirmPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirm)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        // ✅ SEND JSON (THIS FIXES INVALID JSON FORMAT)
        SignupRequest request =
                new SignupRequest(name, email, password);

        apiService.signup(request).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                if (response.isSuccessful()
                        && response.body() != null
                        && "success".equals(response.body().status)) {

                    Toast.makeText(SignUpActivity.this,
                            response.body().message,
                            Toast.LENGTH_SHORT).show();

                    finish(); // back to login

                } else {
                    Toast.makeText(SignUpActivity.this,
                            response.body() != null
                                    ? response.body().message
                                    : "Signup failed",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(SignUpActivity.this,
                        "Error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
