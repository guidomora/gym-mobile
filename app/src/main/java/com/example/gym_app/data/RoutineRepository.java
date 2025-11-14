package com.example.gym_app.data;

import android.content.Context;
import android.text.TextUtils;

import com.example.gym_app.model.Routine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RoutineRepository {

    private final RoutineLocalDataSource routineLocalDataSource;
    private final RoutinePreferencesDataSource routinePreferencesDataSource;

    public RoutineRepository() {
        this(new RoutineLocalDataSource(), new RoutinePreferencesDataSource());
    }

    RoutineRepository(RoutineLocalDataSource routineLocalDataSource,
                      RoutinePreferencesDataSource routinePreferencesDataSource) {
        this.routineLocalDataSource = routineLocalDataSource;
        this.routinePreferencesDataSource = routinePreferencesDataSource;
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
}