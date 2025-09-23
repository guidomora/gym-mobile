package com.example.gym_app;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout; // Importa LinearLayout

public class PerfilEntrenadorActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_entrenador);

        // Referencia a los elementos de la barra de navegación
        LinearLayout homeButton = findViewById(R.id.nav_home);
        LinearLayout profileButton = findViewById(R.id.nav_profile);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navega a la pantalla de Inicio del entrenador
                startActivity(new Intent(PerfilEntrenadorActivity.this, InicioEntrenadorActivity.class));
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Se queda en la misma pantalla (Perfil del entrenador)
                // No necesitas hacer nada, pero puedes poner un Toast si quieres
            }
        });
    }
}