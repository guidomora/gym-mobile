package com.example.gym_app;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class RutinaEntrenadorActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rutina_entrenador);

        // Referencias a los elementos de la barra de navegación del entrenador
        LinearLayout homeButton = findViewById(R.id.nav_home);
        LinearLayout profileButton = findViewById(R.id.nav_profile);

        // Referencia a los botones de acción
        Button addExerciseButton = findViewById(R.id.btn_add_exercise);
        Button saveButton = findViewById(R.id.btn_save_routine);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navega a la pantalla de Inicio del entrenador
                startActivity(new Intent(RutinaEntrenadorActivity.this, InicioEntrenadorActivity.class));
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navega a la pantalla de Perfil del entrenador
                startActivity(new Intent(RutinaEntrenadorActivity.this, PerfilEntrenadorActivity.class));
            }
        });

        addExerciseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para añadir un nuevo ejercicio
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para guardar la rutina
            }
        });
    }
}