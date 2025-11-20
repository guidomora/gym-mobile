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
    private static final String KEY_ROLE = "auth_role";
    private static final String KEY_GYM_NAME = "auth_gym_name";

    SavedLoginData getSavedLoginData(Context context) {
        SharedPreferences preferences = getPreferences(context);
        boolean rememberMe = preferences.getBoolean(KEY_REMEMBER_ME, false);
        String email = preferences.getString(KEY_EMAIL, null);
        String displayName = preferences.getString(KEY_DISPLAY_NAME, null);
        String token = preferences.getString(KEY_TOKEN, null);
        String role = preferences.getString(KEY_ROLE, null);
        String gymName = preferences.getString(KEY_GYM_NAME, null);
        return new SavedLoginData(email, displayName, token, rememberMe, role, gymName);
    }

    void saveSession(Context context,
                     String email,
                     String displayName,
                     String authToken,
                     String role,
                     String gymName,
                     boolean rememberMe) {
        SharedPreferences preferences = getPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
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

        if (rememberMe) {
            editor.putBoolean(KEY_REMEMBER_ME, true);
            editor.putString(KEY_EMAIL, email);
            editor.putString(KEY_DISPLAY_NAME, displayName);
            // Token is already handled above, but the previous logic had it inside rememberMe block too for some reason.
            // The previous logic was:
            /*
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
                editor.remove(KEY_ROLE);
            }
             */
             // This logic implies that if rememberMe is false, we don't save email/displayname, and we remove token/role?
             // Usually token is needed for session regardless of rememberMe (until app kill), but here it seems SharedPreferences is used for persistent session.
             // If rememberMe is false, we probably still want to save the token for the current session if the app relies on SharedPreferences for it.
             // However, following the existing pattern:
        } else {
            editor.putBoolean(KEY_REMEMBER_ME, false);
            editor.remove(KEY_EMAIL);
            editor.remove(KEY_DISPLAY_NAME);
            // It seems if rememberMe is false, it clears everything. But wait, saveSession is called on login.
            // If I login without remember me, I still need the token to make requests.
            // The previous code had:
            /*
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
            if (rememberMe) { ... } else { ... remove(KEY_TOKEN); remove(KEY_ROLE); }
            */
            // This means if rememberMe is false, it writes token/role then immediately removes it in the else block.
            // That looks like a bug in the original code or intended behavior where "remember me" is the only way to persist session.
            // But wait, if I am logged in, I need the token.
            // Let's look at the original code again.

            /*
            SharedPreferences preferences = getPreferences(context);
            SharedPreferences.Editor editor = preferences.edit();
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
                editor.remove(KEY_ROLE);
            }
            editor.apply();
            */
            // Yes, the original code removes token and role if rememberMe is false.
            // This implies the app might rely on memory for session if rememberMe is false, OR it just logs you out immediately if you close the app?
            // But `saveSession` is called.
            // If I change this behavior I might break something, but the user request is about gym name.
            // I should preserve the behavior but add gym name.

             editor.putBoolean(KEY_REMEMBER_ME, false);
             editor.remove(KEY_EMAIL);
             editor.remove(KEY_DISPLAY_NAME);
             editor.remove(KEY_TOKEN);
             editor.remove(KEY_ROLE);
             editor.remove(KEY_GYM_NAME);
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
                .remove(KEY_ROLE)
                .remove(KEY_GYM_NAME)
                .apply();
    }

    private SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
}