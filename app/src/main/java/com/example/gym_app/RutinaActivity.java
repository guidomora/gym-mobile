package com.example.gym_app;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RutinaActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rutina);

        Button saveRoutineButton = findViewById(R.id.btn_save_routine);

        saveRoutineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Simula el guardado y regresa a la pantalla de Rutinas
                finish();
            }
        });
    }
}