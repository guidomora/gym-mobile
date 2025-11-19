package com.example.gym_app.data.auth;

import android.text.TextUtils;

public class SavedLoginData {

    private final String email;
    private final String displayName;
    private final String authToken;
    private final boolean rememberMe;
    private final String role;
    private final String gymName;

    SavedLoginData(String email, String displayName, String authToken, boolean rememberMe, String role, String gymName) {
        this.email = email;
        this.displayName = displayName;
        this.authToken = authToken;
        this.rememberMe = rememberMe;
        this.role = role;
        this.gymName = gymName;
    }

    // Overloaded constructor for compatibility
    SavedLoginData(String email, String displayName, String authToken, boolean rememberMe, String role) {
        this(email, displayName, authToken, rememberMe, role, null);
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        if (!TextUtils.isEmpty(displayName)) {
            return displayName;
        }
        return email;
    }

    public String getAuthToken() {
        return authToken;
    }

    public boolean isRememberMe() {
        return rememberMe;
    }

    public boolean shouldPrefillEmail() {
        return rememberMe && !TextUtils.isEmpty(email);
    }
    public String getRole() {
        return role;
    }

    public String getGymName() {
        return gymName;
    }
}
