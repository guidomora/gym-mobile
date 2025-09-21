package com.example.gym_app;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginButton = findViewById(R.id.btn_login);
        Button registerButton = findViewById(R.id.btn_register_from_login);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Simula el login exitoso y navega a la pantalla de Inicio (alumno)
                startActivity(new Intent(LoginActivity.this, InicioActivity.class));
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegistroActivity.class));
            }
        });
    }
}