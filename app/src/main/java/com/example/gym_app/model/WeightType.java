package com.example.gym_app.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public enum WeightType {
    BARRAS,
    MANCUERNAS,
    RUSAS,
    NINGUNA;

    @NonNull
    public static String toApiValue(@Nullable String value) {
        if (value == null) {
            return NINGUNA.name();
        }
        try {
            return WeightType.valueOf(value.toUpperCase()).name();
        } catch (IllegalArgumentException exception) {
            return NINGUNA.name();
        }
    }
}