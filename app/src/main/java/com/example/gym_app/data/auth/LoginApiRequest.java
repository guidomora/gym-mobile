package com.example.gym_app.data.auth;

import com.google.gson.annotations.SerializedName;

class LoginApiRequest {

    @SerializedName("email")
    private final String email;

    @SerializedName("password")
    private final String password;

    LoginApiRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
