package com.example.gym_app.model;

import androidx.annotation.Nullable;

public class LoginResult {

    private final String email;
    private final String displayName;
    private final String authToken;
    private final String message;

    public LoginResult(String email, String displayName, @Nullable String authToken, @Nullable String message) {
        this.email = email;
        this.displayName = displayName;
        this.authToken = authToken;
        this.message = message;
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
}
