package com.example.gym_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gym_app.adapter.RoutineAdapter;
import com.example.gym_app.data.RoutineRepository;
import com.example.gym_app.data.RoutineLocalDataSource;
import com.example.gym_app.model.Routine;

public class RutinasActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rutinas);

        RecyclerView routinesRecyclerView = findViewById(R.id.rv_routines);
        routinesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        RoutineAdapter routineAdapter = new RoutineAdapter(new RoutineAdapter.OnRoutineClickListener() {
            @Override
            public void onRoutineClick(Routine routine) {
                openRoutineDetail(routine);
            }
        });
        routinesRecyclerView.setAdapter(routineAdapter);

        RoutineRepository routineRepository = new RoutineRepository();
        routineAdapter.submitList(routineRepository.getRoutines(this));

        LinearLayout homeButton = findViewById(R.id.nav_home);
        LinearLayout todayButton = findViewById(R.id.nav_today);
        LinearLayout profileButton = findViewById(R.id.nav_profile);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // No hace nada porque ya estás en la pantalla de inicio (Rutinas)
            }
        });

        todayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RutinasActivity.this, HoyActivity.class));
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RutinasActivity.this, PerfilActivity.class));
            }
        });
    }

    private void openRoutineDetail(Routine routine) {
        Intent intent = new Intent(this, RutinaActivity.class);
        intent.putExtra(RutinaActivity.EXTRA_ROUTINE_ID, routine.getId());
        intent.putExtra(RutinaActivity.EXTRA_ROUTINE_NAME, routine.getName());
        intent.putExtra(RutinaActivity.EXTRA_ROUTINE_DURATION, routine.getDurationInMinutes());
        intent.putExtra(RutinaActivity.EXTRA_ROUTINE_DAY, routine.getDayOfWeek());
        startActivity(intent);
    }
}