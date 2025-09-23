package com.example.gym_app;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout; // Importa LinearLayout

public class PerfilActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        // Referencia a los elementos de la barra de navegación
        LinearLayout homeButton = findViewById(R.id.nav_home);
        LinearLayout todayButton = findViewById(R.id.nav_today);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navega a la pantalla de Inicio (alumno)
                startActivity(new Intent(PerfilActivity.this, InicioActivity.class));
            }
        });

        todayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navega a la pantalla de Hoy (alumno)
                startActivity(new Intent(PerfilActivity.this, HoyActivity.class));
            }
        });
    }
}