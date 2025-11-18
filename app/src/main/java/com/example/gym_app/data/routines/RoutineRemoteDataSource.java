package com.example.gym_app.data.routines;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class RoutineRemoteDataSource {

    private final RoutineApiService apiService;

    RoutineRemoteDataSource() {
        this(RoutineServiceFactory.createService());
    }

    RoutineRemoteDataSource(RoutineApiService apiService) {
        this.apiService = apiService;
    }

    Call<Void> createRoutine(@Nullable String authToken,
                             CreateRoutineRequest request,
                             final RemoteCallback callback) {
        String authHeader = null;
        if (!TextUtils.isEmpty(authToken)) {
            authHeader = "Bearer " + authToken;
        }
        Call<Void> call = apiService.createRoutine(authHeader, request);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                    return;
                }
                callback.onError(extractErrorMessage(response), null);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
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
                // Ignored.
            }
        }
    }

    interface RemoteCallback {
        void onSuccess();

        void onError(@Nullable String errorMessage, @Nullable Throwable throwable);
    }
}
