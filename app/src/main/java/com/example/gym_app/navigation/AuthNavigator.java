package com.example.gym_app.navigation;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.example.gym_app.InicioAdminActivity;
import com.example.gym_app.InicioEntrenadorActivity;
import com.example.gym_app.RutinasActivity;

public final class AuthNavigator {

    private AuthNavigator() {
        // No instances.
    }

    public static void navigateToHome(Activity activity, @Nullable String role, boolean clearTask) {
        Intent intent = new Intent(activity, resolveDestination(role));
        if (clearTask) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        activity.startActivity(intent);
        activity.finish();
    }

    private static Class<?> resolveDestination(@Nullable String role) {
        if (role != null) {
            if ("ADMIN".equalsIgnoreCase(role)) {
                return InicioAdminActivity.class;
            }
            if ("TRAINER".equalsIgnoreCase(role)
                    || "COACH".equalsIgnoreCase(role)
                    || "ENTRENADOR".equalsIgnoreCase(role)) {
                return InicioEntrenadorActivity.class;
            }
        }
        return RutinasActivity.class;
    }
}