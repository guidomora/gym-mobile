package com.example.gym_app;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout; // Importa LinearLayout

public class RutinasActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rutinas);

        LinearLayout pechoCard = findViewById(R.id.card_pecho);

        pechoCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navega a la pantalla para ver la rutina específica
                startActivity(new Intent(RutinasActivity.this, RutinaActivity.class));
            }
        });

        // El resto de la navegación del footer
    }
}