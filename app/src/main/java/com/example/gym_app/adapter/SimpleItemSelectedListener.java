package com.example.gym_app.adapter;

import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.NonNull;

class SimpleItemSelectedListener implements AdapterView.OnItemSelectedListener {

    interface OnItemSelectedCallback {
        void onItemSelected(@NonNull String value);
    }

    private final OnItemSelectedCallback callback;

    SimpleItemSelectedListener(@NonNull OnItemSelectedCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Object item = parent.getItemAtPosition(position);
        if (item != null) {
            callback.onItemSelected(item.toString());
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // No-op
    }
}