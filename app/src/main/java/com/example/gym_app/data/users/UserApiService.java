package com.example.gym_app.data.users;


import androidx.annotation.Nullable;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface UserApiService {

        @GET("/api/users")
        Call<List<UserResponse>> getAllUsers(@Nullable @Header("Authorization") String authHeader);
    }
