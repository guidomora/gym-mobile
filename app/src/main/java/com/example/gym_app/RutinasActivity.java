package com.example.gym_app;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RutinasActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rutinas);

        Button viewRoutineButton = findViewById(R.id.btn_view_routine);
        Button trainerHomeButton = findViewById(R.id.btn_trainer_home_rutinas);
        Button trainerProfileButton = findViewById(R.id.btn_trainer_profile_rutinas);

        viewRoutineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navega a la pantalla para ver una rutina específica
                startActivity(new Intent(RutinasActivity.this, RutinaActivity.class));
            }
        });

        trainerHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Vuelve a la pantalla de Inicio del entrenador
                startActivity(new Intent(RutinasActivity.this, InicioEntrenadorActivity.class));
            }
        });

        trainerProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navega a la pantalla de Perfil del entrenador
                startActivity(new Intent(RutinasActivity.this, PerfilEntrenadorActivity.class));
            }
        });
    }
}