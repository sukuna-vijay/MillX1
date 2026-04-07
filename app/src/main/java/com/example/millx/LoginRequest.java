package com.example.millx;

public class LoginRequest {
    public String email;
    public String password;
    public String role;

    public LoginRequest(String email, String password, String role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }
}
