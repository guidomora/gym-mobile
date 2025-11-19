package com.example.gym_app.data.user;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.Path;

public interface UserApiService {
    @PATCH("api/users/{userId}")
    Call<Void> updateProfile(
            @Header("Authorization") String token,
            @Path("userId") String userId,
            @Body UpdateProfileRequest request
    );
}