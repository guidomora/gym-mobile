package com.example.gym_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gym_app.adapter.ExerciseAdapter;
import com.example.gym_app.data.RoutineLocalDataSource;
import com.example.gym_app.model.Exercise;
import com.example.gym_app.model.Routine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RutinaActivity extends AppCompatActivity {

    public static final String EXTRA_ROUTINE_ID = "extra_routine_id";
    public static final String EXTRA_ROUTINE_NAME = "extra_routine_name";
    public static final String EXTRA_ROUTINE_DURATION = "extra_routine_duration";
    public static final String EXTRA_ROUTINE_DAY = "extra_routine_day";

    private ExerciseAdapter exerciseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rutina);

        ImageButton backButton = findViewById(R.id.btn_back);
        LinearLayout homeButton = findViewById(R.id.nav_home);
        LinearLayout todayButton = findViewById(R.id.nav_today);
        LinearLayout profileButton = findViewById(R.id.nav_profile);
        TextView routineTitle = findViewById(R.id.tv_routine_title);
        TextView routineMeta = findViewById(R.id.tv_routine_meta);
        TextView emptyState = findViewById(R.id.tv_empty_state);
        RecyclerView exercisesRecyclerView = findViewById(R.id.rv_exercises);

        exercisesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        exerciseAdapter = new ExerciseAdapter();
        exercisesRecyclerView.setAdapter(exerciseAdapter);

        Routine routine = loadRoutineData();
        if (routine != null) {
            routineTitle.setText(routine.getName());
            routineMeta.setText(formatRoutineMeta(routine.getDurationInMinutes(), routine.getDayOfWeek()));
            bindExercises(routine.getExercises(), exercisesRecyclerView, emptyState);
        } else {
            String fallbackName = getIntent().getStringExtra(EXTRA_ROUTINE_NAME);
            int fallbackDuration = getIntent().getIntExtra(EXTRA_ROUTINE_DURATION, 0);
            String fallbackDay = getIntent().getStringExtra(EXTRA_ROUTINE_DAY);
            if (fallbackName != null && !fallbackName.isEmpty()) {
                routineTitle.setText(fallbackName);
                routineMeta.setText(formatRoutineMeta(fallbackDuration, fallbackDay));
            }
            bindExercises(Collections.<Exercise>emptyList(), exercisesRecyclerView, emptyState);
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // No se necesita acción ya que el usuario está en la pantalla de inicio
            }
        });

        todayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RutinaActivity.this, HoyActivity.class));
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RutinaActivity.this, PerfilActivity.class));
            }
        });
    }

    private Routine loadRoutineData() {
        Intent intent = getIntent();
        if (intent == null) {
            return null;
        }
        String routineId = intent.getStringExtra(EXTRA_ROUTINE_ID);
        if (routineId == null || routineId.isEmpty()) {
            return null;
        }
        RoutineLocalDataSource dataSource = new RoutineLocalDataSource();
        return dataSource.getRoutineById(this, routineId);
    }

    private void bindExercises(List<Exercise> exercises, RecyclerView recyclerView, TextView emptyState) {
        if (exercises == null || exercises.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
            exerciseAdapter.submitList(Collections.<Exercise>emptyList());
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
            exerciseAdapter.submitList(new ArrayList<>(exercises));
        }
    }

    private String formatRoutineMeta(int duration, String day) {
        if (day == null || day.isEmpty()) {
            return getString(R.string.routine_meta_only_duration, duration);
        }
        return getString(R.string.routine_meta_format, duration, day);
    }
}