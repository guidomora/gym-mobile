package com.example.gym_app.data.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

class AuthPreferencesDataSource {

    private static final String PREF_NAME = "auth_preferences";
    private static final String KEY_EMAIL = "auth_email";
    private static final String KEY_DISPLAY_NAME = "auth_display_name";
    private static final String KEY_TOKEN = "auth_token";
    private static final String KEY_REMEMBER_ME = "auth_remember_me";

    SavedLoginData getSavedLoginData(Context context) {
        SharedPreferences preferences = getPreferences(context);
        boolean rememberMe = preferences.getBoolean(KEY_REMEMBER_ME, false);
        String email = preferences.getString(KEY_EMAIL, null);
        String displayName = preferences.getString(KEY_DISPLAY_NAME, null);
        String token = preferences.getString(KEY_TOKEN, null);
        return new SavedLoginData(email, displayName, token, rememberMe);
    }

    void saveSession(Context context, String email, String displayName, String authToken, boolean rememberMe) {
        SharedPreferences preferences = getPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        if (rememberMe) {
            editor.putBoolean(KEY_REMEMBER_ME, true);
            editor.putString(KEY_EMAIL, email);
            editor.putString(KEY_DISPLAY_NAME, displayName);
            if (!TextUtils.isEmpty(authToken)) {
                editor.putString(KEY_TOKEN, authToken);
            } else {
                editor.remove(KEY_TOKEN);
            }
        } else {
            editor.putBoolean(KEY_REMEMBER_ME, false);
            editor.remove(KEY_EMAIL);
            editor.remove(KEY_DISPLAY_NAME);
            editor.remove(KEY_TOKEN);
        }
        editor.apply();
    }

    void clearSession(Context context) {
        SharedPreferences preferences = getPreferences(context);
        preferences.edit()
                .putBoolean(KEY_REMEMBER_ME, false)
                .remove(KEY_EMAIL)
                .remove(KEY_DISPLAY_NAME)
                .remove(KEY_TOKEN)
                .apply();
    }

    private SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
}
