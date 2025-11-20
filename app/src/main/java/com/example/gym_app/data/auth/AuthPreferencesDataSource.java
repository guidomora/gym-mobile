package com.example.gym_app.data.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

class AuthPreferencesDataSource {

    private static final String PREF_NAME = "auth_preferences";
    private static final String KEY_USER_ID = "auth_user_id";
    private static final String KEY_EMAIL = "auth_email";
    private static final String KEY_DISPLAY_NAME = "auth_display_name";
    private static final String KEY_TOKEN = "auth_token";
    private static final String KEY_REMEMBER_ME = "auth_remember_me";
    private static final String KEY_ROLE = "auth_role";
    private static final String KEY_GYM_NAME = "auth_gym_name";

    SavedLoginData getSavedLoginData(Context context) {
        SharedPreferences preferences = getPreferences(context);
        boolean rememberMe = preferences.getBoolean(KEY_REMEMBER_ME, false);
        long userIdLong = preferences.getLong(KEY_USER_ID, -1L);
        Long userId = userIdLong != -1L ? userIdLong : null;
        String email = preferences.getString(KEY_EMAIL, null);
        String displayName = preferences.getString(KEY_DISPLAY_NAME, null);
        String token = preferences.getString(KEY_TOKEN, null);
        String role = preferences.getString(KEY_ROLE, null);
        String gymName = preferences.getString(KEY_GYM_NAME, null);
        return new SavedLoginData(userId, email, displayName, token, rememberMe, role, gymName);
    }

    void saveSession(Context context,
                     Long userId,
                     String email,
                     String displayName,
                     String authToken,
                     String role,
                     String gymName,
                     boolean rememberMe) {
        SharedPreferences preferences = getPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();

        if (userId != null) {
            editor.putLong(KEY_USER_ID, userId);
        } else {
            editor.remove(KEY_USER_ID);
        }

        if (!TextUtils.isEmpty(authToken)) {
            editor.putString(KEY_TOKEN, authToken);
        } else {
            editor.remove(KEY_TOKEN);
        }
        if (!TextUtils.isEmpty(role)) {
            editor.putString(KEY_ROLE, role);
        } else {
            editor.remove(KEY_ROLE);
        }
        if (!TextUtils.isEmpty(gymName)) {
            editor.putString(KEY_GYM_NAME, gymName);
        } else {
            editor.remove(KEY_GYM_NAME);
        }

        if (!TextUtils.isEmpty(email)) {
            editor.putString(KEY_EMAIL, email);
        } else {
            editor.remove(KEY_EMAIL);
        }

        if (!TextUtils.isEmpty(displayName)) {
            editor.putString(KEY_DISPLAY_NAME, displayName);
        } else {
            editor.remove(KEY_DISPLAY_NAME);
        }

        editor.putBoolean(KEY_REMEMBER_ME, rememberMe);
        
        editor.apply();
    }
    
    // Overload for backward compatibility
    void saveSession(Context context,
                     String email,
                     String displayName,
                     String authToken,
                     String role,
                     String gymName,
                     boolean rememberMe) {
        saveSession(context, null, email, displayName, authToken, role, gymName, rememberMe);
    }

    void clearSession(Context context) {
        SharedPreferences preferences = getPreferences(context);
        preferences.edit()
                .putBoolean(KEY_REMEMBER_ME, false)
                .remove(KEY_USER_ID)
                .remove(KEY_EMAIL)
                .remove(KEY_DISPLAY_NAME)
                .remove(KEY_TOKEN)
                .remove(KEY_ROLE)
                .remove(KEY_GYM_NAME)
                .apply();
    }

    private SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
}