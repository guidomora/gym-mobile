package com.example.gym_app.data.gyms;


import androidx.annotation.Nullable;

import com.example.gym_app.data.users.UserResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface GymApiService {

        @GET("/api/gyms")
        Call<List<GymResponse>> getAllGyms();
    }
