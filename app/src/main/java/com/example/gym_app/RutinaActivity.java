package com.example.gym_app;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class RutinaActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rutina);

        // Referencias a los elementos de la barra de navegación
        ImageButton backButton = findViewById(R.id.btn_back);
        LinearLayout homeButton = findViewById(R.id.nav_home);
        LinearLayout todayButton = findViewById(R.id.nav_today);
        LinearLayout profileButton = findViewById(R.id.nav_profile);

        // Listener para el botón de volver
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navega a la pantalla anterior (Lista de rutinas)
                finish();
            }
        });

        // La navegación del botón de inicio no hace nada, ya que ya estás en esta pantalla
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // No se necesita acción ya que el usuario está en la pantalla de inicio
            }
        });

        todayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navega a la pantalla de Hoy
                startActivity(new Intent(RutinaActivity.this, HoyActivity.class));
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navega a la pantalla de Perfil
                startActivity(new Intent(RutinaActivity.this, PerfilActivity.class));
            }
        });
    }
}