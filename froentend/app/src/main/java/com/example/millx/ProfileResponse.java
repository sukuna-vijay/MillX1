package com.example.millx;

import com.google.gson.annotations.SerializedName;

public class ProfileResponse {
    @SerializedName("status")
    public String status;

    @SerializedName("message")
    public String message;

    @SerializedName("data")
    public UserData data;

    public static class UserData {
        @SerializedName("name")
        public String name;

        @SerializedName("email")
        public String email;

        @SerializedName("phone")
        public String phone;

        @SerializedName("address")
        public String address;

        @SerializedName("profile_image")
        public String profileImage;
    }
}
