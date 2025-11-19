package com.example.gym_app.data.auth;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.gym_app.R;
import com.example.gym_app.RegisterFormData;
import com.example.gym_app.model.LoginCredentials;
import com.example.gym_app.model.LoginResult;

import java.io.IOException;

import retrofit2.Call;

public class AuthRepository {

    private final AuthRemoteDataSource remoteDataSource;
    private final AuthPreferencesDataSource preferencesDataSource;

    private Call<LoginApiResponse> ongoingLoginCall;
    private Call<LoginApiResponse> ongoingRegisterCall;

    public AuthRepository() {
        this(new AuthRemoteDataSource(), new AuthPreferencesDataSource());
    }

    AuthRepository(AuthRemoteDataSource remoteDataSource, AuthPreferencesDataSource preferencesDataSource) {
        this.remoteDataSource = remoteDataSource;
        this.preferencesDataSource = preferencesDataSource;
    }

    public void login(final Context context,
                      final LoginCredentials credentials,
                      final LoginCallback callback) {
        cancelOngoingLogin();
        ongoingLoginCall = remoteDataSource.login(credentials.getEmail(), credentials.getPassword(), new AuthRemoteDataSource.RemoteCallback() {
            @Override
            public void onSuccess(@Nullable LoginApiResponse response) {
                ongoingLoginCall = null;
                LoginResult result = buildLoginResult(credentials, response, null);
                preferencesDataSource.saveSession(context,
                        result.getEmail(),
                        result.getDisplayName(),
                        result.getAuthToken(),
                        result.getRole(),
                        credentials.isRememberMe());
                callback.onSuccess(result);
            }

            @Override
            public void onError(@Nullable String errorMessage, @Nullable Throwable throwable) {
                ongoingLoginCall = null;
                String message = resolveErrorMessage(context, errorMessage, throwable);
                callback.onError(message);
            }
        });
    }

    public void cancelOngoingLogin() {
        if (ongoingLoginCall != null) {
            ongoingLoginCall.cancel();
            ongoingLoginCall = null;
        }
    }

    public void register(final Context context,
                         final RegisterFormData formData,
                         final RegisterCallback callback) {
        cancelOngoingRegister();
        RegisterApiRequest request = new RegisterApiRequest(
                formData.getUsername(),
                formData.getEmail(),
                formData.getPassword(),
                formData.getRole(),
                formData.getPhone(),
                formData.getBirthdate(),
                formData.getGym()
        );
        ongoingRegisterCall = remoteDataSource.register(request, new AuthRemoteDataSource.RemoteCallback() {
            @Override
            public void onSuccess(@Nullable LoginApiResponse response) {
                ongoingRegisterCall = null;
                LoginCredentials credentials = new LoginCredentials(formData.getEmail(), formData.getPassword(), true);
                LoginResult result = buildLoginResult(credentials, response, formData.getRole());
                preferencesDataSource.saveSession(context,
                        result.getEmail(),
                        result.getDisplayName(),
                        result.getAuthToken(),
                        result.getRole(),
                        true);
                callback.onSuccess(result);
            }

            @Override
            public void onError(@Nullable String errorMessage, @Nullable Throwable throwable) {
                ongoingRegisterCall = null;
                String message = resolveErrorMessage(context, errorMessage, throwable);
                callback.onError(message);
            }
        });
    }

    public void cancelOngoingRegister() {
        if (ongoingRegisterCall != null) {
            ongoingRegisterCall.cancel();
            ongoingRegisterCall = null;
        }
    }

    @NonNull
    public SavedLoginData getSavedLoginData(Context context) {
        return preferencesDataSource.getSavedLoginData(context);
    }

    public void clearSession(Context context) {
        preferencesDataSource.clearSession(context);
    }

    private LoginResult buildLoginResult(LoginCredentials credentials,
                                         @Nullable LoginApiResponse response,
                                         @Nullable String fallbackRole) {
        String email = credentials.getEmail();
        String displayName = null;
        String token = null;
        String message = null;
        String role = null;

        if (response != null) {
            String resolvedEmail = response.getResolvedEmail();
            if (!TextUtils.isEmpty(resolvedEmail)) {
                email = resolvedEmail;
            }
            displayName = response.getResolvedDisplayName();
            token = response.getResolvedToken();
            message = response.getMessage();
            role = response.getResolvedRole();
        }

        if (TextUtils.isEmpty(displayName)) {
            displayName = email;
        }

        if (TextUtils.isEmpty(role) && !TextUtils.isEmpty(fallbackRole)) {
            role = fallbackRole;
        }

        return new LoginResult(email, displayName, token, message, role);
    }

    private String resolveErrorMessage(Context context, @Nullable String errorMessage, @Nullable Throwable throwable) {
        if (throwable instanceof IOException) {
            return context.getString(R.string.login_error_network);
        }
        if (!TextUtils.isEmpty(errorMessage)) {
            return errorMessage;
        }
        return context.getString(R.string.login_error_unknown);
    }

    public interface LoginCallback {
        void onSuccess(@NonNull LoginResult result);

        void onError(@NonNull String errorMessage);
    }

    public interface RegisterCallback {
        void onSuccess(@NonNull LoginResult result);

        void onError(@NonNull String errorMessage);
    }
}