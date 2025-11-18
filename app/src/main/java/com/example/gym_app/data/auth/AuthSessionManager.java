package com.example.gym_app.data.auth;

import android.content.Context;

import androidx.annotation.Nullable;

/**
 * Helper class to expose the current authenticated session data to other modules without exposing
 * the persistence implementation.
 */
public class AuthSessionManager {

    private final AuthPreferencesDataSource preferencesDataSource;

    public AuthSessionManager() {
        this(new AuthPreferencesDataSource());
    }

    AuthSessionManager(AuthPreferencesDataSource preferencesDataSource) {
        this.preferencesDataSource = preferencesDataSource;
    }

    @Nullable
    public String getAuthToken(Context context) {
        if (context == null) {
            return null;
        }
        SavedLoginData savedLoginData = preferencesDataSource.getSavedLoginData(context);
        return savedLoginData.getAuthToken();
    }
}
