package com.example.millx;

public class LoginResponse {
    private String status;
    private String message;
    private UserData data;

    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public UserData getData() { return data; }

    public static class UserData {
        private int id;
        private String name;
        private String role;

        public int getId() { return id; }
        public String getName() { return name; }
        public String getRole() { return role; }
    }
}
