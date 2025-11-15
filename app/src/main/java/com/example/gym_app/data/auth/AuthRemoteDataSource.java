package com.example.gym_app.data.auth;

import androidx.annotation.Nullable;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class AuthRemoteDataSource {

    private final AuthApiService apiService;

    AuthRemoteDataSource() {
        this(AuthServiceFactory.createService());
    }

    AuthRemoteDataSource(AuthApiService apiService) {
        this.apiService = apiService;
    }

    Call<LoginApiResponse> login(String email, String password, final RemoteCallback callback) {
        Call<LoginApiResponse> call = apiService.login(new LoginApiRequest(email, password));
        call.enqueue(new Callback<LoginApiResponse>() {
            @Override
            public void onResponse(Call<LoginApiResponse> call, Response<LoginApiResponse> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                    return;
                }
                callback.onError(extractErrorMessage(response), null);
            }

            @Override
            public void onFailure(Call<LoginApiResponse> call, Throwable t) {
                if (call.isCanceled()) {
                    return;
                }
                callback.onError(null, t);
            }
        });
        return call;
    }

    private String extractErrorMessage(Response<?> response) {
        if (response == null || response.errorBody() == null) {
            return null;
        }
        ResponseBody errorBody = response.errorBody();
        try {
            String body = errorBody.string();
            if (body == null || body.isEmpty()) {
                return null;
            }
            JSONObject jsonObject = new JSONObject(body);
            if (jsonObject.has("message")) {
                return jsonObject.optString("message");
            }
            if (jsonObject.has("error")) {
                return jsonObject.optString("error");
            }
            if (jsonObject.has("detail")) {
                return jsonObject.optString("detail");
            }
            return null;
        } catch (Exception exception) {
            return null;
        } finally {
            try {
                errorBody.close();
            } catch (Exception ignored) {
                // Ignored
            }
        }
    }

    interface RemoteCallback {
        void onSuccess(@Nullable LoginApiResponse response);

        void onError(@Nullable String errorMessage, @Nullable Throwable throwable);
    }
}
