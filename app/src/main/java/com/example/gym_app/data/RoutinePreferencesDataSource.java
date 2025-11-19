package com.example.gym_app.data;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

class RoutinePreferencesDataSource {

    private static final String PREF_NAME = "routine_preferences";
    private static final String KEY_DELETED_IDS = "deleted_routine_ids";

    Set<String> getDeletedRoutineIds(Context context) {
        SharedPreferences preferences = getPreferences(context);
        Set<String> storedIds = preferences.getStringSet(KEY_DELETED_IDS, Collections.<String>emptySet());
        if (storedIds == null || storedIds.isEmpty()) {
            return Collections.emptySet();
        }
        return new HashSet<>(storedIds);
    }

    void markRoutineAsDeleted(Context context, String routineId) {
        SharedPreferences preferences = getPreferences(context);
        Set<String> storedIds = preferences.getStringSet(KEY_DELETED_IDS, Collections.<String>emptySet());
        Set<String> updatedIds = new HashSet<>();
        if (storedIds != null) {
            updatedIds.addAll(storedIds);
        }
        if (updatedIds.add(routineId)) {
            preferences.edit().putStringSet(KEY_DELETED_IDS, updatedIds).apply();
        }
    }

    void removeDeletedRoutine(Context context, String routineId) {
        SharedPreferences preferences = getPreferences(context);
        Set<String> storedIds = preferences.getStringSet(KEY_DELETED_IDS, Collections.<String>emptySet());
        if (storedIds == null || storedIds.isEmpty()) {
            return;
        }
        if (!storedIds.contains(routineId)) {
            return;
        }
        Set<String> updatedIds = new HashSet<>(storedIds);
        updatedIds.remove(routineId);
        preferences.edit().putStringSet(KEY_DELETED_IDS, updatedIds).apply();
    }

    private SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
}
