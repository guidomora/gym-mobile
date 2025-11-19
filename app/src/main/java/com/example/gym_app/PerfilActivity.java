package com.example.gym_app;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class PerfilActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        LinearLayout homeButton = findViewById(R.id.nav_home);
        LinearLayout todayButton = findViewById(R.id.nav_today);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ahora navega a RutinasActivity, que es la nueva pantalla de inicio
                startActivity(new Intent(PerfilActivity.this, RutinasActivity.class));
            }
        });

        todayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PerfilActivity.this, HoyActivity.class));
            }
        });
    }
}