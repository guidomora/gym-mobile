package com.example.gym_app.data.user;

import com.google.gson.annotations.SerializedName;

public class UpdateProfileRequest {
    @SerializedName("name")
    private final String name;

    @SerializedName("phoneNumber")
    private final String phoneNumber;

    @SerializedName("birthdate")
    private final String birthdate; // Formato: "yyyy-MM-dd"

    public UpdateProfileRequest(String name, String phoneNumber, String birthdate) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.birthdate = birthdate;
    }
}