package com.example.gym_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gym_app.adapter.RoutineAdapter;
import com.example.gym_app.data.RoutineRepository;
import com.example.gym_app.model.Routine;

import java.util.List;

public class RutinasActivity extends AppCompatActivity {

    private RoutineRepository routineRepository;
    private RoutineAdapter routineAdapter;
    private ProgressBar progressBar;
    private TextView emptyStateTextView;
    private RecyclerView routinesRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rutinas);

        routinesRecyclerView = findViewById(R.id.rv_routines);
        progressBar = findViewById(R.id.progress_bar); // Agregar en tu layout
        emptyStateTextView = findViewById(R.id.tv_empty_state); // Agregar en tu layout

        routinesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        routineRepository = new RoutineRepository();

        routineAdapter = new RoutineAdapter(new RoutineAdapter.OnRoutineClickListener() {
            @Override
            public void onRoutineClick(Routine routine) {
                openRoutineDetail(routine);
            }
        });
        routinesRecyclerView.setAdapter(routineAdapter);

        LinearLayout homeButton = findViewById(R.id.nav_home);
        LinearLayout todayButton = findViewById(R.id.nav_today);
        LinearLayout profileButton = findViewById(R.id.nav_profile);

        homeButton.setOnClickListener(v -> {
            // Ya estás en home
        });

        todayButton.setOnClickListener(v ->
                startActivity(new Intent(RutinasActivity.this, HoyActivity.class)));

        profileButton.setOnClickListener(v ->
                startActivity(new Intent(RutinasActivity.this, PerfilActivity.class)));

        // Cargar rutinas desde la API
        loadRoutinesFromApi();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar rutinas cuando vuelves a la activity
        loadRoutinesFromApi();
    }

    private void loadRoutinesFromApi() {
        showLoading(true);

        routineRepository.getAllRoutines(this, new RoutineRepository.GetAllRoutinesCallback() {
            @Override
            public void onSuccess(List<Routine> routines) {
                showLoading(false);
                if (routines == null || routines.isEmpty()) {
                    showEmptyState(true);
                } else {
                    showEmptyState(false);
                    routineAdapter.submitList(routines);
                }
            }

            @Override
            public void onError(@NonNull String errorMessage) {
                showLoading(false);
                Toast.makeText(RutinasActivity.this,
                        errorMessage,
                        Toast.LENGTH_LONG).show();
                // Fallback: cargar rutinas locales si falla la API
                loadLocalRoutines();
            }
        });
    }

    private void loadLocalRoutines() {
        List<Routine> localRoutines = routineRepository.getRoutines(this);
        if (localRoutines == null || localRoutines.isEmpty()) {
            showEmptyState(true);
        } else {
            showEmptyState(false);
            routineAdapter.submitList(localRoutines);
        }
    }

    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (routinesRecyclerView != null) {
            routinesRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void showEmptyState(boolean show) {
        if (emptyStateTextView != null) {
            emptyStateTextView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (routinesRecyclerView != null) {
            routinesRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void openRoutineDetail(Routine routine) {
        Intent intent = new Intent(this, RutinaActivity.class);
        intent.putExtra(RutinaActivity.EXTRA_ROUTINE_ID, routine.getId());
        intent.putExtra(RutinaActivity.EXTRA_ROUTINE_NAME, routine.getName());
        intent.putExtra(RutinaActivity.EXTRA_ROUTINE_DURATION, routine.getDurationInMinutes());
        intent.putExtra(RutinaActivity.EXTRA_ROUTINE_DAY, routine.getDayOfWeek());
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        routineRepository.cancelRoutineCreation();
        super.onDestroy();
    }
}