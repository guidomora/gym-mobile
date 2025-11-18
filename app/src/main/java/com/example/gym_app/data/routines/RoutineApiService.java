package com.example.gym_app.data.routines;

import androidx.annotation.Nullable;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

interface RoutineApiService {

    @POST("api/routines")
    Call<Void> createRoutine(@Nullable @Header("Authorization") String authHeader,
                             @Body CreateRoutineRequest request);
}
