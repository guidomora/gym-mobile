package com.example.gym_app.data.auth;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

class RegisterApiRequest {

    @SerializedName("name")
    private final String name;

    @SerializedName("email")
    private final String email;

    @SerializedName("password")
    private final String password;

    @SerializedName("role")
    private final String role;

    @SerializedName("phoneNumber")
    @Nullable
    private final String phoneNumber;

    @SerializedName("birthdate")
    private final String birthdate;

    @SerializedName("gymName")
    private final String gymName;

    RegisterApiRequest(String name,
                       String email,
                       String password,
                       String role,
                       @Nullable String phoneNumber,
                       String birthdate,
                       String gymName) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.phoneNumber = phoneNumber;
        this.birthdate = birthdate;
        this.gymName = gymName;
    }
}