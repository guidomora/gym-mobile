package com.example.gym_app;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class InicioEntrenadorActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_entrenador);

        // Referencias a los botones de "Ver" de los alumnos
        Button viewJohnDoe = findViewById(R.id.btn_view_john_doe);
        Button viewRobertoPerez = findViewById(R.id.btn_view_roberto_perez);
        Button viewJuanaDoe = findViewById(R.id.btn_view_juana_doe);

        // Referencia al botón de Perfil de la barra de navegación
        LinearLayout profileButton = findViewById(R.id.nav_profile);

        // Configurar el click para cada botón "Ver"
        viewJohnDoe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navega a la pantalla de Rutinas del entrenador
                startActivity(new Intent(InicioEntrenadorActivity.this, RutinasEntrenadorActivity.class));
            }
        });

        viewRobertoPerez.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navega a la pantalla de Rutinas del entrenador
                startActivity(new Intent(InicioEntrenadorActivity.this, RutinasEntrenadorActivity.class));
            }
        });

        viewJuanaDoe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navega a la pantalla de Rutinas del entrenador
                startActivity(new Intent(InicioEntrenadorActivity.this, RutinasEntrenadorActivity.class));
            }
        });

        // Configurar el click para el botón de Perfil
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navega a la pantalla de Perfil del entrenador
                startActivity(new Intent(InicioEntrenadorActivity.this, PerfilEntrenadorActivity.class));
            }
        });
    }
}