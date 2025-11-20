package com.example.gym_app.data.exercises;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ExerciseApiService {

    @POST("api/exercises")
    Call<ExerciseResponse> createExercise(
            @Header("Authorization") String authorization,
            @Body ExerciseRequest request
    );

    @PATCH("api/exercises/{id}")
    Call<ExerciseResponse> updateExercise(
            @Header("Authorization") String authorization,
            @Path("id") Long id,
            @Body ExerciseRequest request
    );

    @DELETE("api/exercises/{id}")
    Call<Void> deleteExercise(
            @Header("Authorization") String authorization,
            @Path("id") Long id
    );

    @GET("api/exercises/{id}")
    Call<ExerciseResponse> getExerciseById(
            @Header("Authorization") String authorization,
            @Path("id") Long id
    );

    @GET("api/exercises")
    Call<List<ExerciseResponse>> getExercises(
            @Header("Authorization") String authorization
    );
}