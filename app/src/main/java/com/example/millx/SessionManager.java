package com.example.millx;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "MillXPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_ID = "user_id";
    private static final String KEY_NAME = "user_name";
    private static final String KEY_ROLE = "role";
    private static final String KEY_TOKEN = "auth_token";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLoginSession(int id, String name, String role, String phone, String profileImage, String token) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_ID, id);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_ROLE, role);
        editor.putString("user_phone", phone);
        editor.putString("user_image", profileImage);
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    public void updateSession(String name, String phone, String profileImage) {
        editor.putString(KEY_NAME, name);
        editor.putString("user_phone", phone);
        if (profileImage != null && !profileImage.isEmpty()) {
            editor.putString("user_image", profileImage);
        }
        editor.apply();
    }

    // Kept for backward compatibility if needed, or remove if unused elsewhere
    public void updateName(String name) {
        editor.putString(KEY_NAME, name);
        editor.apply();
    }

    public String getUserName() {
        return pref.getString(KEY_NAME, "User");
    }

    public String getUserPhone() {
        return pref.getString("user_phone", "");
    }

    public String getUserImage() {
        return pref.getString("user_image", "");
    }

    public int getUserId() {
        return pref.getInt(KEY_ID, -1);
    }

    public void setLogin(boolean isLoggedIn, String role) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.putString(KEY_ROLE, role);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getRole() {
        return pref.getString(KEY_ROLE, "");
    }

    public String getAuthToken() {
        return pref.getString(KEY_TOKEN, "");
    }

    public void logoutUser() {
        editor.clear();
        editor.apply();
    }
}
