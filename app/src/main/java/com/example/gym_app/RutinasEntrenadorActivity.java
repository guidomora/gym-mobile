package com.example.gym_app;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class RutinasEntrenadorActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rutinas_entrenador);

        // Referencias a los botones de la pantalla
        ImageButton backButton = findViewById(R.id.btn_back);
        Button addRoutineButton = findViewById(R.id.btn_add_routine);

        // Referencias a los botones de la barra de navegación
        LinearLayout homeButton = findViewById(R.id.nav_home);
        LinearLayout profileButton = findViewById(R.id.nav_profile);

        // Lógica de navegación del botón de volver
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navega de regreso a la pantalla de Inicio del entrenador
                finish();
            }
        });

        // Lógica de navegación del botón "Rutina +"
        addRoutineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navega a la pantalla para crear una nueva rutina
                startActivity(new Intent(RutinasEntrenadorActivity.this, RutinaEntrenadorActivity.class));
            }
        });

        // Lógica de navegación de la barra inferior
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navega a la pantalla de Inicio del entrenador
                startActivity(new Intent(RutinasEntrenadorActivity.this, InicioEntrenadorActivity.class));
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navega a la pantalla de Perfil del entrenador
                startActivity(new Intent(RutinasEntrenadorActivity.this, PerfilEntrenadorActivity.class));
            }
        });
    }
}