package com.example.gym_app.data;

import android.content.Context;
import android.text.TextUtils;

import com.example.gym_app.R;
import com.example.gym_app.data.auth.AuthSessionManager;
import com.example.gym_app.data.routines.CreateRoutineRequest;
import com.example.gym_app.data.routines.RoutineRemoteDataSource;
import com.example.gym_app.model.Routine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RoutineRepository {

    private final RoutineLocalDataSource routineLocalDataSource;
    private final RoutinePreferencesDataSource routinePreferencesDataSource;
    private final RoutineRemoteDataSource routineRemoteDataSource;
    private final AuthSessionManager authSessionManager;

    private retrofit2.Call<Void> ongoingCreateRoutineCall;

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
            if (routine == null) {
                continue;
            }
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

    public void createRoutine(Context context,
                              CreateRoutineRequest request,
                              final CreateRoutineCallback callback) {
        if (context == null || request == null || callback == null) {
            return;
        }
        cancelRoutineCreation();
        String authToken = authSessionManager.getAuthToken(context);
        ongoingCreateRoutineCall = routineRemoteDataSource.createRoutine(authToken, request,
                new RoutineRemoteDataSource.RemoteCallback() {
                    @Override
                    public void onSuccess() {
                        ongoingCreateRoutineCall = null;
                        callback.onSuccess();
                    }

                    @Override
                    public void onError(@Nullable String errorMessage, @Nullable Throwable throwable) {
                        ongoingCreateRoutineCall = null;
                        String resolvedMessage = resolveCreateRoutineError(context, errorMessage, throwable);
                        callback.onError(resolvedMessage);
                    }
                });
    }

    public void cancelRoutineCreation() {
        if (ongoingCreateRoutineCall != null) {
            ongoingCreateRoutineCall.cancel();
            ongoingCreateRoutineCall = null;
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
            if (routine == null) {
                continue;
            }
            String id = routine.getId();
            if (TextUtils.isEmpty(id) || !deletedIds.contains(id)) {
                filteredRoutines.add(routine);
            }
        }
        return filteredRoutines;
    }

    private String resolveCreateRoutineError(Context context,
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

    public interface CreateRoutineCallback {
        void onSuccess();

        void onError(@NonNull String errorMessage);
    }
}