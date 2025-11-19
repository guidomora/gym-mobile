package com.example.gym_app;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class HoyActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hoy);

        LinearLayout homeButton = findViewById(R.id.nav_home);
        LinearLayout profileButton = findViewById(R.id.nav_profile);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ahora navega a RutinasActivity, que es la nueva pantalla de inicio
                startActivity(new Intent(HoyActivity.this, RutinasActivity.class));
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HoyActivity.this, PerfilActivity.class));
            }
        });
    }
}