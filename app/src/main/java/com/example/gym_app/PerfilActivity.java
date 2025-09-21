package com.example.gym_app;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PerfilActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        Button homeButton = findViewById(R.id.btn_home_profile);
        Button todayButton = findViewById(R.id.btn_today_profile);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PerfilActivity.this, InicioActivity.class));
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