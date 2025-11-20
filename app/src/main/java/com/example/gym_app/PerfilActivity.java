package com.example.gym_app;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;


public class PerfilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        LinearLayout homeButton = findViewById(R.id.nav_home);
        LinearLayout todayButton = findViewById(R.id.nav_today);
        Button logoutButton = findViewById(R.id.btn_save);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PerfilActivity.this, RutinasActivity.class));
            }
        });

        todayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PerfilActivity.this, HoyActivity.class));
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }
    
    private void logout() {
         // Como AuthPreferencesDataSource es package-private, usamos una clase helper si existiera o
         // idealmente deberiamos exponer el metodo logout en AuthRepository o AuthSessionManager.
         // Dado que no tenemos acceso directo a AuthPreferencesDataSource aqui (es package-private)
         // y AuthSessionManager no tiene logout, vamos a usar SharedPreferences directamente o
         // modificar AuthSessionManager/AuthRepository.
         
         // Verificando archivos disponibles, AuthRepository parece ser el lugar adecuado, pero
         // AuthPreferencesDataSource tiene clearSession. AuthRepository no expone clearSession.
         // Voy a usar el nombre de las shared preferences que vi en AuthPreferencesDataSource
         // para limpiarlas manualmente, O mejor aun,
         // Como no puedo modificar facilmente AuthRepository para agregar logout sin ver si ya lo tiene (no lo vi),
         // voy a limpiar las shared preferences manualmente usando el nombre que vi "auth_preferences".
         
         getSharedPreferences("auth_preferences", MODE_PRIVATE).edit().clear().apply();
         
         Intent intent = new Intent(PerfilActivity.this, LoginActivity.class);
         intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
         startActivity(intent);
         finish();
    }
}