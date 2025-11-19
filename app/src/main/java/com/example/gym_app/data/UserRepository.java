package com.example.gym_app.data;

import android.content.Context;
import com.example.gym_app.data.auth.AuthSessionManager;
import com.example.gym_app.data.user.UpdateProfileRequest;
import com.example.gym_app.data.user.UserApiService;
import com.example.gym_app.data.user.UserServiceFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {

    private final UserApiService apiService;
    private final AuthSessionManager sessionManager;

    public UserRepository() {
        this.apiService = UserServiceFactory.createService();
        this.sessionManager = new AuthSessionManager();
    }

    public void updateProfile(Context context, String userId, UpdateProfileRequest request, final UpdateCallback callback) {
        String token = sessionManager.getAuthToken(context);
        if (token == null) {
            callback.onError("No hay sesión activa");
            return;
        }
        String authHeader = "Bearer " + token;

        apiService.updateProfile(authHeader, userId, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onError("Error al actualizar: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError("Error de conexión: " + t.getMessage());
            }
        });
    }

    public interface UpdateCallback {
        void onSuccess();
        void onError(String error);
    }
}