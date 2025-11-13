package com.example.gym_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gym_app.adapter.EditableExerciseAdapter;
import com.example.gym_app.data.RoutineLocalDataSource;
import com.example.gym_app.model.EditableExercise;
import com.example.gym_app.model.Exercise;
import com.example.gym_app.model.Routine;

import java.util.ArrayList;
import java.util.List;

public class RutinaEntrenadorActivity extends AppCompatActivity {

    public static final String EXTRA_ROUTINE_ID = "extra_trainer_routine_id";
    public static final String EXTRA_ROUTINE_NAME = "extra_trainer_routine_name";
    public static final String EXTRA_ROUTINE_DAY = "extra_trainer_routine_day";

    private final RoutineLocalDataSource routineLocalDataSource = new RoutineLocalDataSource();

    private EditableExerciseAdapter exerciseAdapter;
    private EditText routineNameEditText;
    private Spinner routineDaySpinner;
    private TextView emptyStateTextView;
    private TextView exercisesHeaderTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rutina_entrenador);

        routineNameEditText = findViewById(R.id.et_routine_name);
        routineDaySpinner = findViewById(R.id.spinner_days);
        emptyStateTextView = findViewById(R.id.tv_empty_state);
        exercisesHeaderTextView = findViewById(R.id.tv_exercises_title);
        RecyclerView exercisesRecyclerView = findViewById(R.id.rv_exercises);
        Button addExerciseButton = findViewById(R.id.btn_add_exercise);
        Button saveButton = findViewById(R.id.btn_save);
        LinearLayout homeButton = findViewById(R.id.nav_home);
        LinearLayout profileButton = findViewById(R.id.nav_profile);

        exerciseAdapter = new EditableExerciseAdapter(this::updateEmptyState);
        exercisesRecyclerView.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                // Disable the internal scrolling behaviour so the ScrollView can
                // calculate the full height of the list and display every item.
                return false;
            }
        });
        exercisesRecyclerView.setNestedScrollingEnabled(false);
        exercisesRecyclerView.setAdapter(exerciseAdapter);

        bindRoutine(loadRoutine());

        addExerciseButton.setOnClickListener(v -> {
            exerciseAdapter.addExercise(new EditableExercise());
        });

        saveButton.setOnClickListener(v -> {
            List<Exercise> exercises = buildExerciseSnapshot();
            String routineName = routineNameEditText.getText() == null
                    ? "" : routineNameEditText.getText().toString().trim();
            String displayName = routineName.isEmpty()
                    ? getString(R.string.trainer_routine_placeholder)
                    : routineName;
            Toast.makeText(this,
                    getString(R.string.trainer_routine_saved_message,
                            displayName, exercises.size()),
                    Toast.LENGTH_LONG).show();
        });

        homeButton.setOnClickListener(v ->
                startActivity(new Intent(RutinaEntrenadorActivity.this, InicioEntrenadorActivity.class)));

        profileButton.setOnClickListener(v ->
                startActivity(new Intent(RutinaEntrenadorActivity.this, PerfilEntrenadorActivity.class)));
    }

    private void bindRoutine(@Nullable Routine routine) {
        if (routine != null) {
            routineNameEditText.setText(routine.getName());
            selectDayInSpinner(routine.getDayOfWeek());
            exerciseAdapter.setExercises(mapExercises(routine.getExercises()));
        } else {
            Intent intent = getIntent();
            if (intent != null) {
                String fallbackName = intent.getStringExtra(EXTRA_ROUTINE_NAME);
                String fallbackDay = intent.getStringExtra(EXTRA_ROUTINE_DAY);
                if (!TextUtils.isEmpty(fallbackName)) {
                    routineNameEditText.setText(fallbackName);
                }
                if (!TextUtils.isEmpty(fallbackDay)) {
                    selectDayInSpinner(fallbackDay);
                }
            }
            exerciseAdapter.setExercises(new ArrayList<>());
        }
        updateEmptyState(exerciseAdapter.getItemCount());
    }

    @Nullable
    private Routine loadRoutine() {
        Intent intent = getIntent();
        if (intent == null) {
            return null;
        }
        String routineId = intent.getStringExtra(EXTRA_ROUTINE_ID);
        if (TextUtils.isEmpty(routineId)) {
            return null;
        }
        return routineLocalDataSource.getRoutineById(this, routineId);
    }

    private List<Exercise> buildExerciseSnapshot() {
        List<EditableExercise> editableExercises = exerciseAdapter.getExercisesSnapshot();
        List<Exercise> exercises = new ArrayList<>(editableExercises.size());
        for (EditableExercise editableExercise : editableExercises) {
            exercises.add(editableExercise.toExercise());
        }
        return exercises;
    }

    private List<EditableExercise> mapExercises(@Nullable List<Exercise> exercises) {
        List<EditableExercise> editableExercises = new ArrayList<>();
        if (exercises != null) {
            for (Exercise exercise : exercises) {
                editableExercises.add(EditableExercise.fromExercise(exercise));
            }
        }
        return editableExercises;
    }

    private void selectDayInSpinner(@Nullable String day) {
        if (routineDaySpinner == null || TextUtils.isEmpty(day)) {
            return;
        }
        String[] days = getResources().getStringArray(R.array.routine_days);
        for (int index = 0; index < days.length; index++) {
            if (day.equalsIgnoreCase(days[index])) {
                routineDaySpinner.setSelection(index);
                return;
            }
        }
    }

    private void updateEmptyState(int itemCount) {
        if (emptyStateTextView != null) {
            emptyStateTextView.setVisibility(itemCount == 0 ? View.VISIBLE : View.GONE);
        }
        updateExercisesHeader(itemCount);
    }

    private void updateExercisesHeader(int itemCount) {
        if (exercisesHeaderTextView == null) {
            return;
        }
        String headerText = getResources().getQuantityString(
                R.plurals.trainer_exercises_header, itemCount, itemCount);
        exercisesHeaderTextView.setText(headerText);
    }
}