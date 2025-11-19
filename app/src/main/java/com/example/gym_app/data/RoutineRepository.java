package com.example.gym_app.data;

import android.content.Context;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.gym_app.R;
import com.example.gym_app.data.auth.AuthSessionManager;
import com.example.gym_app.data.routines.CreateRoutineRequest;
import com.example.gym_app.data.routines.RoutineRemoteDataSource;
import com.example.gym_app.data.routines.RoutineResponse;
import com.example.gym_app.data.routines.UpdateRoutineRequest;
import com.example.gym_app.model.Exercise;
import com.example.gym_app.model.Routine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RoutineRepository {

    private final RoutineLocalDataSource routineLocalDataSource;
    private final RoutinePreferencesDataSource routinePreferencesDataSource;
    private final RoutineRemoteDataSource routineRemoteDataSource;
    private final AuthSessionManager authSessionManager;

    private retrofit2.Call<?> ongoingCall;

    public RoutineRepository() {
        this(new RoutineLocalDataSource(),
                new RoutinePreferencesDataSource(),
                new RoutineRemoteDataSource(),
                new AuthSessionManager());
    }

    RoutineRepository(RoutineLocalDataSource routineLocalDataSource,
                      RoutinePreferencesDataSource routinePreferencesDataSource,
                      RoutineRemoteDataSource routineRemoteDataSource,
                      AuthSessionManager authSessionManager) {
        this.routineLocalDataSource = routineLocalDataSource;
        this.routinePreferencesDataSource = routinePreferencesDataSource;
        this.routineRemoteDataSource = routineRemoteDataSource;
        this.authSessionManager = authSessionManager;
    }

    // OBTENER TODAS LAS RUTINAS (desde API)
    public void getAllRoutines(Context context, final GetAllRoutinesCallback callback) {
        if (context == null || callback == null) {
            return;
        }
        cancelOngoingCall();
        String authToken = authSessionManager.getAuthToken(context);

        ongoingCall = routineRemoteDataSource.getAllRoutines(authToken,
                new RoutineRemoteDataSource.GetAllRoutinesCallback() {
                    @Override
                    public void onSuccess(List<RoutineResponse> routines) {
                        ongoingCall = null;
                        List<Routine> mappedRoutines = mapRoutineResponses(routines);
                        callback.onSuccess(mappedRoutines);
                    }

                    @Override
                    public void onError(@Nullable String errorMessage, @Nullable Throwable throwable) {
                        ongoingCall = null;
                        String resolvedMessage = resolveError(context, errorMessage, throwable);
                        callback.onError(resolvedMessage);
                    }
                });
    }

    // OBTENER RUTINA POR ID (desde API)
    public void getRoutineById(Context context, Long routineId, final GetRoutineCallback callback) {
        if (context == null || routineId == null || callback == null) {
            return;
        }
        cancelOngoingCall();
        String authToken = authSessionManager.getAuthToken(context);

        ongoingCall = routineRemoteDataSource.getRoutineById(authToken, routineId,
                new RoutineRemoteDataSource.GetRoutineCallback() {
                    @Override
                    public void onSuccess(RoutineResponse response) {
                        ongoingCall = null;
                        Routine routine = mapRoutineResponse(response);
                        callback.onSuccess(routine);
                    }

                    @Override
                    public void onError(@Nullable String errorMessage, @Nullable Throwable throwable) {
                        ongoingCall = null;
                        String resolvedMessage = resolveError(context, errorMessage, throwable);
                        callback.onError(resolvedMessage);
                    }
                });
    }

    // CREAR RUTINA (API)
    public void createRoutine(Context context,
                              CreateRoutineRequest request,
                              final CreateRoutineCallback callback) {
        if (context == null || request == null || callback == null) {
            return;
        }
        cancelOngoingCall();
        String authToken = authSessionManager.getAuthToken(context);

        ongoingCall = routineRemoteDataSource.createRoutine(authToken, request,
                new RoutineRemoteDataSource.CreateRoutineCallback() {
                    @Override
                    public void onSuccess(RoutineResponse response) {
                        ongoingCall = null;
                        callback.onSuccess();
                    }

                    @Override
                    public void onError(@Nullable String errorMessage, @Nullable Throwable throwable) {
                        ongoingCall = null;
                        String resolvedMessage = resolveError(context, errorMessage, throwable);
                        callback.onError(resolvedMessage);
                    }
                });
    }

    // ACTUALIZAR RUTINA (API)
    public void updateRoutine(Context context,
                              Long routineId,
                              UpdateRoutineRequest request,
                              final UpdateRoutineCallback callback) {
        if (context == null || routineId == null || request == null || callback == null) {
            return;
        }
        cancelOngoingCall();
        String authToken = authSessionManager.getAuthToken(context);

        ongoingCall = routineRemoteDataSource.updateRoutine(authToken, routineId, request,
                new RoutineRemoteDataSource.UpdateRoutineCallback() {
                    @Override
                    public void onSuccess(RoutineResponse response) {
                        ongoingCall = null;
                        callback.onSuccess();
                    }

                    @Override
                    public void onError(@Nullable String errorMessage, @Nullable Throwable throwable) {
                        ongoingCall = null;
                        String resolvedMessage = resolveError(context, errorMessage, throwable);
                        callback.onError(resolvedMessage);
                    }
                });
    }

    // ELIMINAR RUTINA (API)
    public void deleteRoutine(Context context, Long routineId, final DeleteRoutineCallback callback) {
        if (context == null || routineId == null || callback == null) {
            return;
        }
        cancelOngoingCall();
        String authToken = authSessionManager.getAuthToken(context);

        ongoingCall = routineRemoteDataSource.deleteRoutine(authToken, routineId,
                new RoutineRemoteDataSource.DeleteRoutineCallback() {
                    @Override
                    public void onSuccess() {
                        ongoingCall = null;
                        callback.onSuccess();
                    }

                    @Override
                    public void onError(@Nullable String errorMessage, @Nullable Throwable throwable) {
                        ongoingCall = null;
                        String resolvedMessage = resolveError(context, errorMessage, throwable);
                        callback.onError(resolvedMessage);
                    }
                });
    }

    // MÉTODOS LOCALES (para compatibilidad con código existente)
    public List<Routine> getRoutines(Context context) {
        List<Routine> routines = routineLocalDataSource.getRoutines(context);
        return filterDeletedRoutines(context, routines);
    }

    public Routine getRoutineById(Context context, String routineId) {
        if (TextUtils.isEmpty(routineId)) {
            return null;
        }
        Routine routine = routineLocalDataSource.getRoutineById(context, routineId);
        if (routine == null) {
            return null;
        }
        Set<String> deletedIds = routinePreferencesDataSource.getDeletedRoutineIds(context);
        if (deletedIds.contains(routineId)) {
            return null;
        }
        return routine;
    }

    public List<Routine> getRoutinesByIds(Context context, List<String> routineIds) {
        if (routineIds == null || routineIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<Routine> allRoutines = routineLocalDataSource.getRoutines(context);
        if (allRoutines.isEmpty()) {
            return Collections.emptyList();
        }
        Set<String> deletedIds = routinePreferencesDataSource.getDeletedRoutineIds(context);
        Map<String, Routine> routinesById = new HashMap<>();
        for (Routine routine : allRoutines) {
            if (routine == null) continue;
            String id = routine.getId();
            if (!TextUtils.isEmpty(id) && !deletedIds.contains(id)) {
                routinesById.put(id, routine);
            }
        }
        List<Routine> selectedRoutines = new ArrayList<>();
        for (String routineId : routineIds) {
            Routine routine = routinesById.get(routineId);
            if (routine != null) {
                selectedRoutines.add(routine);
            }
        }
        return selectedRoutines;
    }

    public void getRoutinesByUserId(Context context, Long userId, final GetAllRoutinesCallback callback) {
        if (context == null || userId == null || callback == null) {
            return;
        }
        cancelOngoingCall();
        String authToken = authSessionManager.getAuthToken(context);

        // Asumiendo que agregaste este método en tu RemoteDataSource similar a los otros
        ongoingCall = routineRemoteDataSource.getRoutinesByUserId(authToken, userId,
                new RoutineRemoteDataSource.GetAllRoutinesCallback() {
                    @Override
                    public void onSuccess(List<RoutineResponse> routines) {
                        ongoingCall = null;
                        // Mapeamos la respuesta de la API al modelo local Routine
                        List<Routine> mappedRoutines = mapRoutineResponses(routines);
                        callback.onSuccess(mappedRoutines);
                    }

                    @Override
                    public void onError(@Nullable String errorMessage, @Nullable Throwable throwable) {
                        ongoingCall = null;
                        String resolvedMessage = resolveError(context, errorMessage, throwable);
                        callback.onError(resolvedMessage);
                    }
                });
    }

    public boolean deleteRoutine(Context context, String routineId) {
        if (TextUtils.isEmpty(routineId)) {
            return false;
        }
        Routine routine = routineLocalDataSource.getRoutineById(context, routineId);
        if (routine == null) {
            return false;
        }
        routinePreferencesDataSource.markRoutineAsDeleted(context, routineId);
        return true;
    }

    public void restoreRoutine(Context context, String routineId) {
        if (TextUtils.isEmpty(routineId)) {
            return;
        }
        routinePreferencesDataSource.removeDeletedRoutine(context, routineId);
    }

    public void cancelRoutineCreation() {
        cancelOngoingCall();
    }

    private void cancelOngoingCall() {
        if (ongoingCall != null) {
            ongoingCall.cancel();
            ongoingCall = null;
        }
    }

    private List<Routine> filterDeletedRoutines(Context context, List<Routine> routines) {
        if (routines == null || routines.isEmpty()) {
            return Collections.emptyList();
        }
        Set<String> deletedIds = routinePreferencesDataSource.getDeletedRoutineIds(context);
        if (deletedIds.isEmpty()) {
            return routines;
        }
        List<Routine> filteredRoutines = new ArrayList<>(routines.size());
        for (Routine routine : routines) {
            if (routine == null) continue;
            String id = routine.getId();
            if (TextUtils.isEmpty(id) || !deletedIds.contains(id)) {
                filteredRoutines.add(routine);
            }
        }
        return filteredRoutines;
    }

    // Mapear RoutineResponse a Routine
    private Routine mapRoutineResponse(RoutineResponse response) {
        if (response == null) return null;

        return new Routine(
                String.valueOf(response.getId()),
                response.getName(),
                0, // duration (puedes calcularlo o agregarlo al DTO)
                response.getDayOfWeek(),
                new ArrayList<Exercise>() // Los ejercicios los manejas por separado
        );
    }

    private List<Routine> mapRoutineResponses(List<RoutineResponse> responses) {
        if (responses == null) return Collections.emptyList();

        List<Routine> routines = new ArrayList<>(responses.size());
        for (RoutineResponse response : responses) {
            routines.add(mapRoutineResponse(response));
        }
        return routines;
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

    // CALLBACKS
    public interface CreateRoutineCallback {
        void onSuccess();
        void onError(@NonNull String errorMessage);
    }

    public interface GetRoutineCallback {
        void onSuccess(Routine routine);
        void onError(@NonNull String errorMessage);
    }

    public interface GetAllRoutinesCallback {
        void onSuccess(List<Routine> routines);
        void onError(@NonNull String errorMessage);
    }

    public interface UpdateRoutineCallback {
        void onSuccess();
        void onError(@NonNull String errorMessage);
    }

    public interface DeleteRoutineCallback {
        void onSuccess();
        void onError(@NonNull String errorMessage);
    }
}