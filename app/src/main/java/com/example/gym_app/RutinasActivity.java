package com.example.gym_app;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class RutinasActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rutinas);

        // Referencias a los elementos de la barra de navegación
        LinearLayout homeButton = findViewById(R.id.nav_home);
        LinearLayout todayButton = findViewById(R.id.nav_today);
        LinearLayout profileButton = findViewById(R.id.nav_profile);

        // Ya estás en la pantalla de Rutinas, no necesitas un listener para este botón
        // Opcionalmente, puedes dejarlo vacío o mostrar un Toast.
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // No hace nada porque ya estás en la pantalla de inicio (Rutinas)
            }
        });

        todayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navega a la pantalla de Hoy
                startActivity(new Intent(RutinasActivity.this, HoyActivity.class));
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navega a la pantalla de Perfil
                startActivity(new Intent(RutinasActivity.this, PerfilActivity.class));
            }
        });
    }
}