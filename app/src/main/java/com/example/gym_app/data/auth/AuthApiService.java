package com.example.gym_app.data.auth;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

interface AuthApiService {

    @Headers({"Content-Type: application/json"})
    @POST("api/auth/login")
    Call<LoginApiResponse> login(@Body LoginApiRequest request);
}
