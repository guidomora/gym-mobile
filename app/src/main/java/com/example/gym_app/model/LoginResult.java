package com.example.gym_app.model;

import androidx.annotation.Nullable;

public class LoginResult {

    private final Long userId;
    private final String email;
    private final String displayName;
    private final String authToken;
    private final String message;
    private final String role;
    private final String gymName;

    public LoginResult(@Nullable Long userId,
                       String email,
                       String displayName,
                       @Nullable String authToken,
                       @Nullable String message,
                       @Nullable String role,
                       @Nullable String gymName) {
        this.userId = userId;
        this.email = email;
        this.displayName = displayName;
        this.authToken = authToken;
        this.message = message;
        this.role = role;
        this.gymName = gymName;
    }

    // Constructor for backward compatibility
    public LoginResult(String email,
                       String displayName,
                       @Nullable String authToken,
                       @Nullable String message,
                       @Nullable String role,
                       @Nullable String gymName) {
        this(null, email, displayName, authToken, message, role, gymName);
    }

    // Constructor for backward compatibility
    public LoginResult(String email,
                       String displayName,
                       @Nullable String authToken,
                       @Nullable String message,
                       @Nullable String role) {
        this(null, email, displayName, authToken, message, role, null);
    }

    @Nullable
    public Long getUserId() {
        return userId;
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

    @Nullable
    public String getGymName() {
        return gymName;
    }
}