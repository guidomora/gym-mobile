package com.example.gym_app;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PerfilEntrenadorActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_entrenador);

        Button homeButton = findViewById(R.id.btn_trainer_home_profile);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navega a la pantalla de Inicio del entrenador
                startActivity(new Intent(PerfilEntrenadorActivity.this, InicioEntrenadorActivity.class));
            }
        });
    }
}