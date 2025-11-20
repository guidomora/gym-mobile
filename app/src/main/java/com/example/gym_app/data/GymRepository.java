package com.example.gym_app.data;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.gym_app.R;
import com.example.gym_app.data.auth.AuthSessionManager;
import com.example.gym_app.data.gyms.GymRemoteDataSource;
import com.example.gym_app.data.gyms.GymResponse;
import com.example.gym_app.data.users.UserRemoteDataSource;
import com.example.gym_app.data.users.UserResponse;

import java.io.IOException;
import java.util.List;

public class GymRepository {

    private final GymRemoteDataSource gymRemoteDataSource;
    private final AuthSessionManager authSessionManager;

    private retrofit2.Call<?> ongoingCall;

    public GymRepository() {
        this(new GymRemoteDataSource(),
                new AuthSessionManager());
    }

    GymRepository(GymRemoteDataSource gymRemoteDataSource,
                  AuthSessionManager authSessionManager) {
        this.gymRemoteDataSource = gymRemoteDataSource;
        this.authSessionManager = authSessionManager;
    }

    public void getAllGyms(Context context, final GetAllGymsCallback callback) {
        if (context == null || callback == null) {
            return;
        }
        cancelOngoingCall();
        String authToken = authSessionManager.getAuthToken(context);

        ongoingCall = gymRemoteDataSource.getAllGyms(authToken,
                new GymRemoteDataSource.GetAllGymsCallback() {
                    @Override
                    public void onSuccess(List<GymResponse> gyms) {
                        ongoingCall = null;
                        callback.onSuccess(gyms);
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
            return context.getString(R.string.trainer_routine_create_error_network);
        }
        if (!TextUtils.isEmpty(errorMessage)) {
            return errorMessage;
        }
        return context.getString(R.string.trainer_routine_create_error_generic);
    }

    public interface GetAllGymsCallback {
        void onSuccess(List<GymResponse> gyms);
        void onError(@NonNull String errorMessage);
    }
}