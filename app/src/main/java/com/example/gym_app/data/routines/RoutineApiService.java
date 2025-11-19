package com.example.gym_app.data.routines;

import androidx.annotation.Nullable;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

interface RoutineApiService {

    @POST("api/routines")
    Call<RoutineResponse> createRoutine(
            @Nullable @Header("Authorization") String authHeader,
            @Body CreateRoutineRequest request
    );

    @GET("api/routines/{id}")
    Call<RoutineResponse> getRoutineById(
            @Nullable @Header("Authorization") String authHeader,
            @Path("id") Long id
    );

    @GET("api/routines")
    Call<List<RoutineResponse>> getAllRoutines(
            @Nullable @Header("Authorization") String authHeader
    );

    @PATCH("api/routines/{id}")
    Call<RoutineResponse> updateRoutine(
            @Nullable @Header("Authorization") String authHeader,
            @Path("id") Long id,
            @Body UpdateRoutineRequest request
    );

    @DELETE("api/routines/{id}")
    Call<Void> deleteRoutine(
            @Nullable @Header("Authorization") String authHeader,
            @Path("id") Long id
    );

    @GET("api/routines/user/{userId}")
    Call<List<RoutineResponse>> getRoutinesByUserId(
            @Nullable @Header("Authorization") String authHeader,
            @Path("userId") Long userId
    );
}