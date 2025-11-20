package com.example.gym_app.data.user;

import com.example.gym_app.data.users.UserResponse;

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

    @PATCH("api/users/membership")
    Call<UserResponse> linkMembership(
            @Header("Authorization") String token,
            @Body String membershipKey
    );
}