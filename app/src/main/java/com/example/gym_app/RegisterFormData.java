package com.example.gym_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RegisterFormData {
    private final String username;
    @Nullable
    private final String phone;
    private final String email;
    private final String birthdate;
    private final String password;
    private final String gym;

    public RegisterFormData(@NonNull String username,
                            @Nullable String phone,
                            @NonNull String email,
                            @NonNull String birthdate,
                            @NonNull String password,
                            @NonNull String gym) {
        this.username = username;
        this.phone = phone;
        this.email = email;
        this.birthdate = birthdate;
        this.password = password;
        this.gym = gym;
    }

    @NonNull
    public String getUsername() {
        return username;
    }

    @Nullable
    public String getPhone() {
        return phone;
    }

    @NonNull
    public String getEmail() {
        return email;
    }

    @NonNull
    public String getBirthdate() {
        return birthdate;
    }

    @NonNull
    public String getPassword() {
        return password;
    }

    @NonNull
    public String getGym() {
        return gym;
    }
}