package com.example.gym_app.data.routines;

import android.text.TextUtils;
import androidx.annotation.Nullable;
import org.json.JSONObject;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// MANTENEMOS 'public' (De Nico)
public class RoutineRemoteDataSource {

    private final RoutineApiService apiService;

    // MANTENEMOS 'public' (De Nico)
    public RoutineRemoteDataSource() {
        this(RoutineServiceFactory.createService());
    }

    RoutineRemoteDataSource(RoutineApiService apiService) {
        this.apiService = apiService;
    }

    // --- MÉTODOS DE LA RAMA MASTER (Más completos) ---

    // CREAR RUTINA
    public Call<RoutineResponse> createRoutine(@Nullable String authToken,
                                               CreateRoutineRequest request,
                                               final CreateRoutineCallback callback) {
        String authHeader = buildAuthHeader(authToken);
        Call<RoutineResponse> call = apiService.createRoutine(authHeader, request);
        call.enqueue(new Callback<RoutineResponse>() {
            @Override
            public void onResponse(Call<RoutineResponse> call, Response<RoutineResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                    return;
                }
                callback.onError(extractErrorMessage(response), null);
            }

            @Override
            public void onFailure(Call<RoutineResponse> call, Throwable t) {
                if (call.isCanceled()) {
                    return;
                }
                callback.onError(null, t);
            }
        });
        return call;
    }

    // OBTENER RUTINA POR ID
    public Call<RoutineResponse> getRoutineById(@Nullable String authToken,
                                                Long id,
                                                final GetRoutineCallback callback) {
        String authHeader = buildAuthHeader(authToken);
        Call<RoutineResponse> call = apiService.getRoutineById(authHeader, id);
        call.enqueue(new Callback<RoutineResponse>() {
            @Override
            public void onResponse(Call<RoutineResponse> call, Response<RoutineResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                    return;
                }
                callback.onError(extractErrorMessage(response), null);
            }

            @Override
            public void onFailure(Call<RoutineResponse> call, Throwable t) {
                if (call.isCanceled()) {
                    return;
                }
                callback.onError(null, t);
            }
        });
        return call;
    }

    public Call<List<RoutineResponse>> getRoutinesByUserId(@Nullable String authToken, Long userId,
                                                           final GetAllRoutinesCallback callback) {
        String authHeader = buildAuthHeader(authToken);
        Call<List<RoutineResponse>> call = apiService.getRoutinesByUserId(authHeader, userId);
        call.enqueue(new Callback<List<RoutineResponse>>() {
            @Override
            public void onResponse(Call<List<RoutineResponse>> call, Response<List<RoutineResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                    return;
                }
                callback.onError(extractErrorMessage(response), null);
            }

            @Override
            public void onFailure(Call<List<RoutineResponse>> call, Throwable t) {
                if (call.isCanceled()) {
                    return;
                }
                callback.onError(null, t);
            }
        });
        return call;
    }

    // OBTENER TODAS LAS RUTINAS
    public Call<List<RoutineResponse>> getAllRoutines(@Nullable String authToken,
                                                      final GetAllRoutinesCallback callback) {
        String authHeader = buildAuthHeader(authToken);
        Call<List<RoutineResponse>> call = apiService.getAllRoutines(authHeader);
        call.enqueue(new Callback<List<RoutineResponse>>() {
            @Override
            public void onResponse(Call<List<RoutineResponse>> call, Response<List<RoutineResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                    return;
                }
                callback.onError(extractErrorMessage(response), null);
            }

            @Override
            public void onFailure(Call<List<RoutineResponse>> call, Throwable t) {
                if (call.isCanceled()) {
                    return;
                }
                callback.onError(null, t);
            }
        });
        return call;
    }

    // ACTUALIZAR RUTINA
    public Call<RoutineResponse> updateRoutine(@Nullable String authToken,
                                               Long id,
                                               UpdateRoutineRequest request,
                                               final UpdateRoutineCallback callback) {
        String authHeader = buildAuthHeader(authToken);
        Call<RoutineResponse> call = apiService.updateRoutine(authHeader, id, request);
        call.enqueue(new Callback<RoutineResponse>() {
            @Override
            public void onResponse(Call<RoutineResponse> call, Response<RoutineResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                    return;
                }
                callback.onError(extractErrorMessage(response), null);
            }

            @Override
            public void onFailure(Call<RoutineResponse> call, Throwable t) {
                if (call.isCanceled()) {
                    return;
                }
                callback.onError(null, t);
            }
        });
        return call;
    }

    // ELIMINAR RUTINA
    public Call<Void> deleteRoutine(@Nullable String authToken,
                                    Long id,
                                    final DeleteRoutineCallback callback) {
        String authHeader = buildAuthHeader(authToken);
        Call<Void> call = apiService.deleteRoutine(authHeader, id);
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

    // CALLBACKS (Mantenemos 'public' de Nico y la estructura de Master)
    public interface CreateRoutineCallback {
        void onSuccess(RoutineResponse routine);
        void onError(@Nullable String errorMessage, @Nullable Throwable throwable);
    }

    public interface GetRoutineCallback {
        void onSuccess(RoutineResponse routine);
        void onError(@Nullable String errorMessage, @Nullable Throwable throwable);
    }

    public interface GetAllRoutinesCallback {
        void onSuccess(List<RoutineResponse> routines);
        void onError(@Nullable String errorMessage, @Nullable Throwable throwable);
    }

    public interface UpdateRoutineCallback {
        void onSuccess(RoutineResponse routine);
        void onError(@Nullable String errorMessage, @Nullable Throwable throwable);
    }

    public interface DeleteRoutineCallback {
        void onSuccess();
        void onError(@Nullable String errorMessage, @Nullable Throwable throwable);
    }
}