package com.example.gym_app.data.users;

import android.text.TextUtils;
import androidx.annotation.Nullable;
import org.json.JSONObject;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRemoteDataSource {

    private final UserApiService apiService;

    public UserRemoteDataSource() {
        this(UserServiceFactory.createService());
    }

    UserRemoteDataSource(UserApiService apiService) {
        this.apiService = apiService;
    }

    public Call<List<UserResponse>> getAllUsers(@Nullable String authToken,
                                                final GetAllUsersCallback callback) {
        String authHeader = buildAuthHeader(authToken);
        Call<List<UserResponse>> call = apiService.getAllUsers(authHeader);

        call.enqueue(new Callback<List<UserResponse>>() {
            @Override
            public void onResponse(Call<List<UserResponse>> call, Response<List<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                    return;
                }
                callback.onError(extractErrorMessage(response), null);
            }

            @Override
            public void onFailure(Call<List<UserResponse>> call, Throwable t) {
                if (call.isCanceled()) {
                    return;
                }
                callback.onError(null, t);
            }
        });
        return call;
    }

    @Nullable
    private String buildAuthHeader(@Nullable String authToken) {
        if (TextUtils.isEmpty(authToken)) {
            return null;
        }
        return "Bearer " + authToken;
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
            }
        }
    }

    public interface GetAllUsersCallback {
        void onSuccess(List<UserResponse> users);
        void onError(@Nullable String errorMessage, @Nullable Throwable throwable);
    }
}