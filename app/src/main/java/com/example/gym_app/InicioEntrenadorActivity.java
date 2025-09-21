package com.example.gym_app;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class InicioEntrenadorActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_entrenador);

        Button viewStudentButton = findViewById(R.id.btn_view_student1);
        Button profileButton = findViewById(R.id.btn_trainer_profile);

        viewStudentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navega a la pantalla de Rutinas para ese alumno
                startActivity(new Intent(InicioEntrenadorActivity.this, RutinasActivity.class));
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navega a la pantalla de Perfil del entrenador
                startActivity(new Intent(InicioEntrenadorActivity.this, PerfilEntrenadorActivity.class));
            }
        });
    }
}