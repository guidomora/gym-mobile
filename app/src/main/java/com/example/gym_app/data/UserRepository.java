package com.example.gym_app.data;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.gym_app.R;
import com.example.gym_app.data.auth.AuthSessionManager;
import com.example.gym_app.data.users.UserRemoteDataSource;
import com.example.gym_app.data.users.UserResponse; // Asegúrate de tener este import

import java.io.IOException;
import java.util.List;

public class UserRepository {

    private final UserRemoteDataSource userRemoteDataSource;
    private final AuthSessionManager authSessionManager;

    private retrofit2.Call<?> ongoingCall;

    public UserRepository() {
        this(new UserRemoteDataSource(),
                new AuthSessionManager());
    }

    UserRepository(UserRemoteDataSource userRemoteDataSource,
                   AuthSessionManager authSessionManager) {
        // CORREGIDO: Asignación correcta
        this.userRemoteDataSource = userRemoteDataSource;
        this.authSessionManager = authSessionManager;
    }

    // OBTENER TODOS LOS USUARIOS (desde API)
    public void getAllUsers(Context context, final GetAllUsersCallback callback) {
        if (context == null || callback == null) {
            return;
        }
        cancelOngoingCall();
        String authToken = authSessionManager.getAuthToken(context);

        ongoingCall = userRemoteDataSource.getAllUsers(authToken,
                new UserRemoteDataSource.GetAllUsersCallback() {
                    @Override
                    public void onSuccess(List<UserResponse> users) {
                        ongoingCall = null;
                        // Si necesitas mapear de UserResponse a un modelo User, hazlo aquí.
                        // Por ahora devolvemos la respuesta directa.
                        callback.onSuccess(users);
                    }

                    @Override
                    public void onError(@Nullable String errorMessage, @Nullable Throwable throwable) {
                        ongoingCall = null;
                        String resolvedMessage = resolveError(context, errorMessage, throwable);
                        callback.onError(resolvedMessage);
                    }
                });
    }

    private void cancelOngoingCall() {
        if (ongoingCall != null) {
            ongoingCall.cancel();
            ongoingCall = null;
        }
    }

    private String resolveError(Context context,
                                @Nullable String errorMessage,
                                @Nullable Throwable throwable) {
        if (throwable instanceof IOException) {
            // Puedes crear un string genérico en strings.xml tipo "error_network"
            return context.getString(R.string.trainer_routine_create_error_network);
        }
        if (!TextUtils.isEmpty(errorMessage)) {
            return errorMessage;
        }
        return context.getString(R.string.trainer_routine_create_error_generic);
    }

    public interface GetAllUsersCallback {
        void onSuccess(List<UserResponse> users);
        void onError(@NonNull String errorMessage);
    }
}