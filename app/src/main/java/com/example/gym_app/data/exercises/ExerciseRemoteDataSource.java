
package com.example.gym_app.data.exercises;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import org.json.JSONObject;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExerciseRemoteDataSource {

    private final ExerciseApiService apiService;

    public ExerciseRemoteDataSource() {
        this(ExerciseServiceFactory.createService());
    }

    ExerciseRemoteDataSource(ExerciseApiService apiService) {
        this.apiService = apiService;
    }

    public Call<ExerciseResponse> createExercise(@Nullable String authToken,
                                                 ExerciseRequest request,
                                                 final CreateExerciseCallback callback) {
        String authHeader = buildAuthHeader(authToken);
        Call<ExerciseResponse> call = apiService.createExercise(authHeader, request);
        call.enqueue(new Callback<ExerciseResponse>() {
            @Override
            public void onResponse(Call<ExerciseResponse> call, Response<ExerciseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                    return;
                }
                callback.onError(extractErrorMessage(response), null);
            }

            @Override
            public void onFailure(Call<ExerciseResponse> call, Throwable t) {
                if (call.isCanceled()) {
                    return;
                }
                callback.onError(null, t);
            }
        });
        return call;
    }

    public Call<ExerciseResponse> updateExercise(@Nullable String authToken,
                                                 Long id,
                                                 ExerciseRequest request,
                                                 final UpdateExerciseCallback callback) {
        String authHeader = buildAuthHeader(authToken);
        Call<ExerciseResponse> call = apiService.updateExercise(authHeader, id, request);
        call.enqueue(new Callback<ExerciseResponse>() {
            @Override
            public void onResponse(Call<ExerciseResponse> call, Response<ExerciseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                    return;
                }
                callback.onError(extractErrorMessage(response), null);
            }

            @Override
            public void onFailure(Call<ExerciseResponse> call, Throwable t) {
                if (call.isCanceled()) {
                    return;
                }
                callback.onError(null, t);
            }
        });
        return call;
    }

    public Call<Void> deleteExercise(@Nullable String authToken,
                                     Long id,
                                     final DeleteExerciseCallback callback) {
        String authHeader = buildAuthHeader(authToken);
        Call<Void> call = apiService.deleteExercise(authHeader, id);
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

    public Call<List<ExerciseResponse>> getExercises(@Nullable String authToken,
                                                     final GetExercisesCallback callback) {
        String authHeader = buildAuthHeader(authToken);
        Call<List<ExerciseResponse>> call = apiService.getExercises(authHeader);
        call.enqueue(new Callback<List<ExerciseResponse>>() {
            @Override
            public void onResponse(Call<List<ExerciseResponse>> call, Response<List<ExerciseResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                    return;
                }
                callback.onError(extractErrorMessage(response), null);
            }

            @Override
            public void onFailure(Call<List<ExerciseResponse>> call, Throwable t) {
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

    public interface CreateExerciseCallback {
        void onSuccess(ExerciseResponse exercise);
        void onError(@Nullable String errorMessage, @Nullable Throwable throwable);
    }

    public interface UpdateExerciseCallback {
        void onSuccess(ExerciseResponse exercise);
        void onError(@Nullable String errorMessage, @Nullable Throwable throwable);
    }

    public interface DeleteExerciseCallback {
        void onSuccess();
        void onError(@Nullable String errorMessage, @Nullable Throwable throwable);
    }

    public interface GetExercisesCallback {
        void onSuccess(List<ExerciseResponse> exercises);
        void onError(@Nullable String errorMessage, @Nullable Throwable throwable);
    }
}