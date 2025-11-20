package com.example.gym_app.data;

import android.content.Context;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.gym_app.R;
import com.example.gym_app.data.auth.AuthSessionManager;
import com.example.gym_app.data.user.UpdateProfileRequest;
import com.example.gym_app.data.user.UserApiService;
import com.example.gym_app.data.user.UserServiceFactory;
import com.example.gym_app.data.users.UserRemoteDataSource;
import com.example.gym_app.data.users.UserResponse;
import java.io.IOException;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {

    private final UserRemoteDataSource userRemoteDataSource;
    private final UserApiService apiService;
    private final AuthSessionManager sessionManager;
    private retrofit2.Call<?> ongoingCall;

    public UserRepository() {
        this.userRemoteDataSource = new UserRemoteDataSource();
        this.apiService = UserServiceFactory.createService();
        this.sessionManager = new AuthSessionManager();
    }

    // --- LISTAR USUARIOS (Master) ---
    public void getAllUsers(Context context, final GetAllUsersCallback callback) {
        if (context == null || callback == null) return;
        cancelOngoingCall();
        String authToken = sessionManager.getAuthToken(context);

        ongoingCall = userRemoteDataSource.getAllUsers(authToken,
                new UserRemoteDataSource.GetAllUsersCallback() {
                    @Override
                    public void onSuccess(List<UserResponse> users) {
                        ongoingCall = null;
                        callback.onSuccess(users);
                    }
                    @Override
                    public void onError(@Nullable String errorMessage, @Nullable Throwable throwable) {
                        ongoingCall = null;
                        callback.onError(resolveError(context, errorMessage, throwable));
                    }
                });
    }

    // --- ACTUALIZAR PERFIL (Nico) ---
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

    // --- VINCULAR MEMBRESÍA (Corrección Asíncrona) ---
    public void linkMembership(Context context, String membershipKey, final UpdateCallback callback) {
        String token = sessionManager.getAuthToken(context);
        if (token == null) {
            callback.onError("No hay sesión activa");
            return;
        }
        String authHeader = "Bearer " + token;

        // Usamos enqueue para NO bloquear el hilo principal
        apiService.linkMembership(authHeader, membershipKey).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onError("Error al vincular: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                callback.onError("Error de red: " + t.getMessage());
            }
        });
    }

    // --- HELPERS & INTERFACES ---
    private void cancelOngoingCall() {
        if (ongoingCall != null) {
            ongoingCall.cancel();
            ongoingCall = null;
        }
    }

    private String resolveError(Context context, @Nullable String errorMessage, @Nullable Throwable throwable) {
        if (throwable instanceof IOException) {
            return context.getString(R.string.trainer_routine_create_error_network);
        }
        return !TextUtils.isEmpty(errorMessage) ? errorMessage : context.getString(R.string.trainer_routine_create_error_generic);
    }

    public interface GetAllUsersCallback {
        void onSuccess(List<UserResponse> users);
        void onError(@NonNull String errorMessage);
    }

    public interface UpdateCallback {
        void onSuccess();
        void onError(String error);
    }
}