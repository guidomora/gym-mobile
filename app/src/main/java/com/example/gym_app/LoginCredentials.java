package com.example.gym_app;

import androidx.annotation.NonNull;

public class LoginCredentials {
    private final String username;
    private final String password;
    private final boolean rememberMe;

    public LoginCredentials(@NonNull String username, @NonNull String password, boolean rememberMe) {
        this.username = username;
        this.password = password;
        this.rememberMe = rememberMe;
    }

    @NonNull
    public String getUsername() {
        return username;
    }

    @NonNull
    public String getPassword() {
        return password;
    }

    public boolean isRememberMe() {
        return rememberMe;
    }
}