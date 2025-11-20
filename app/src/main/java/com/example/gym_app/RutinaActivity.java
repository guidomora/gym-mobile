package com.example.gym_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gym_app.adapter.ExerciseAdapter;
import com.example.gym_app.data.RoutineRepository;
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

    private RoutineRepository routineRepository;
    private ExerciseAdapter exerciseAdapter;
    private TextView routineTitle;
    private TextView routineMeta;
    private TextView emptyState;
    private RecyclerView exercisesRecyclerView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rutina);

        ImageButton backButton = findViewById(R.id.btn_back);
        LinearLayout homeButton = findViewById(R.id.nav_home);
        LinearLayout todayButton = findViewById(R.id.nav_today);
        LinearLayout profileButton = findViewById(R.id.nav_profile);
        routineTitle = findViewById(R.id.tv_routine_title);
        routineMeta = findViewById(R.id.tv_routine_meta);
        emptyState = findViewById(R.id.tv_empty_state);
        progressBar = findViewById(R.id.progress_bar);
        exercisesRecyclerView = findViewById(R.id.rv_exercises);

        exercisesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        exerciseAdapter = new ExerciseAdapter();
        exercisesRecyclerView.setAdapter(exerciseAdapter);

        routineRepository = new RoutineRepository();

        String routineId = getIntent().getStringExtra(EXTRA_ROUTINE_ID);
        if (!TextUtils.isEmpty(routineId)) {
            try {
                Long routineIdLong = Long.parseLong(routineId);
                loadRoutineFromApi(routineIdLong);
            } catch (NumberFormatException e) {
                loadRoutineLocal();
            }
        } else {
            loadRoutineFromExtras();
        }

        backButton.setOnClickListener(v -> finish());

        homeButton.setOnClickListener(v -> {
        });

        todayButton.setOnClickListener(v ->
                startActivity(new Intent(RutinaActivity.this, HoyActivity.class)));

        profileButton.setOnClickListener(v ->
                startActivity(new Intent(RutinaActivity.this, PerfilActivity.class)));
    }

    private void loadRoutineFromApi(Long routineId) {
        showLoading(true);

        routineRepository.getRoutineById(this, routineId, new RoutineRepository.GetRoutineCallback() {
            @Override
            public void onSuccess(Routine routine) {
                showLoading(false);
                displayRoutine(routine);
            }

            @Override
            public void onError(@NonNull String errorMessage) {
                showLoading(false);
                Toast.makeText(RutinaActivity.this,
                        errorMessage,
                        Toast.LENGTH_SHORT).show();
                loadRoutineLocal();
            }
        });
    }

    private void loadRoutineLocal() {
        Routine routine = loadRoutineData();
        if (routine != null) {
            displayRoutine(routine);
        } else {
            loadRoutineFromExtras();
        }
    }

    private void displayRoutine(Routine routine) {
        if (routine != null) {
            routineTitle.setText(routine.getName());
            routineMeta.setText(formatRoutineMeta(routine.getDurationInMinutes(), routine.getDayOfWeek()));
            bindExercises(routine.getExercises());
        } else {
            loadRoutineFromExtras();
        }
    }

    private void loadRoutineFromExtras() {
        String fallbackName = getIntent().getStringExtra(EXTRA_ROUTINE_NAME);
        int fallbackDuration = getIntent().getIntExtra(EXTRA_ROUTINE_DURATION, 0);
        String fallbackDay = getIntent().getStringExtra(EXTRA_ROUTINE_DAY);

        if (fallbackName != null && !fallbackName.isEmpty()) {
            routineTitle.setText(fallbackName);
            routineMeta.setText(formatRoutineMeta(fallbackDuration, fallbackDay));
        } else {
            routineTitle.setText(R.string.routine_not_found);
            routineMeta.setText("");
        }
        bindExercises(Collections.emptyList());
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
        return routineRepository.getRoutineById(this, routineId);
    }

    private void bindExercises(List<Exercise> exercises) {
        if (exercises == null || exercises.isEmpty()) {
            exercisesRecyclerView.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
            exerciseAdapter.submitList(Collections.emptyList());
        } else {
            exercisesRecyclerView.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
            exerciseAdapter.submitList(new ArrayList<>(exercises));
        }
    }

    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (exercisesRecyclerView != null) {
            exercisesRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
        if (emptyState != null && !show) {
            emptyState.setVisibility(View.GONE);
        }
    }

    private String formatRoutineMeta(int duration, String day) {
        if (day == null || day.isEmpty()) {
            return getString(R.string.routine_meta_only_duration, duration);
        }
        return getString(R.string.routine_meta_format, duration, day);
    }

    @Override
    protected void onDestroy() {
        if (routineRepository != null) {
            routineRepository.cancelRoutineCreation();
        }
        super.onDestroy();
    }
}