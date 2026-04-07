package com.example.millx;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    private String status;
    private String message;
    private String token;

    @SerializedName("user")
    private UserData data;

    // getters
    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }

    public UserData getData() {
        return data;
    }

    // inner user object
    public static class UserData {
        private int id;
        private String name;
        private String role;

        @SerializedName("phone")
        private String phone;

        @SerializedName("profile_image")
        private String profileImage;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getRole() {
            return role;
        }

        public String getPhone() {
            return phone;
        }

        public String getProfileImage() {
            return profileImage;
        }
    }
}
