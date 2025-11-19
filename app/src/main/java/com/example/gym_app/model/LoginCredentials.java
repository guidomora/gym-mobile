package com.example.gym_app.model;

import androidx.annotation.NonNull;

public class LoginCredentials {
    private final String email;
    private final String password;
    private final boolean rememberMe;

    public LoginCredentials(@NonNull String email, @NonNull String password, boolean rememberMe) {
        this.email = email;
        this.password = password;
        this.rememberMe = rememberMe;
    }

    @NonNull
    public String getEmail() {
        return email;
    }

    @NonNull
    public String getPassword() {
        return password;
    }

    public boolean isRememberMe() {
        return rememberMe;
    }
}