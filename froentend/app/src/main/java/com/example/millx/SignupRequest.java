package com.example.millx;

public class SignupRequest {
    public String name;
    public String email;
    public String phone;
    public String password;

    public SignupRequest(String name, String email, String phone, String password) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }
}
