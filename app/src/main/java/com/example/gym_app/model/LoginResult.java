package com.example.gym_app.model;

import androidx.annotation.Nullable;

public class LoginResult {

    private final String email;
    private final String displayName;
    private final String authToken;
    private final String message;

    private final String role;

    public LoginResult(String email,
                       String displayName,
                       @Nullable String authToken,
                       @Nullable String message,
                       @Nullable String role) {
        this.email = email;
        this.displayName = displayName;
        this.authToken = authToken;
        this.message = message;
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Nullable
    public String getAuthToken() {
        return authToken;
    }

    @Nullable
    public String getMessage() {
        return message;
    }

    @Nullable
    public String getRole() {
        return role;
    }
}
