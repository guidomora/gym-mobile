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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider; // Necesario para MVVM
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gym_app.adapter.EditableExerciseAdapter;
import com.example.gym_app.data.RoutineRepository;
import com.example.gym_app.data.routines.CreateRoutineRequest;
import com.example.gym_app.model.EditableExercise;
import com.example.gym_app.model.Exercise;
import com.example.gym_app.model.Routine;
import com.example.gym_app.viewmodel.CreateRoutineViewModel; // Tu nuevo ViewModel

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RutinaEntrenadorActivity extends AppCompatActivity {

    public static final String EXTRA_ROUTINE_ID = "extra_trainer_routine_id";
    public static final String EXTRA_ROUTINE_NAME = "extra_trainer_routine_name";
    public static final String EXTRA_ROUTINE_DAY = "extra_trainer_routine_day";
    public static final String EXTRA_STUDENT_ID = "extra_trainer_student_id";

    private CreateRoutineViewModel viewModel;

    private EditableExerciseAdapter exerciseAdapter;
    private EditText routineNameEditText;
    private Spinner routineDaySpinner;
    private TextView emptyStateTextView;
    private TextView exercisesHeaderTextView;
    private Button saveButton;
    private CharSequence saveButtonOriginalText;

    @Nullable
    private String studentId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rutina_entrenador);

        viewModel = new ViewModelProvider(this).get(CreateRoutineViewModel.class);

        routineNameEditText = findViewById(R.id.et_routine_name);
        routineDaySpinner = findViewById(R.id.spinner_days);
        emptyStateTextView = findViewById(R.id.tv_empty_state);
        exercisesHeaderTextView = findViewById(R.id.tv_exercises_title);
        RecyclerView exercisesRecyclerView = findViewById(R.id.rv_exercises);
        Button addExerciseButton = findViewById(R.id.btn_add_exercise);
        saveButton = findViewById(R.id.btn_save);
        LinearLayout homeButton = findViewById(R.id.nav_home);
        LinearLayout profileButton = findViewById(R.id.nav_profile);

        exerciseAdapter = new EditableExerciseAdapter(this::updateEmptyState);
        exercisesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        exercisesRecyclerView.setHasFixedSize(false);
        exercisesRecyclerView.setNestedScrollingEnabled(false);
        exercisesRecyclerView.setAdapter(exerciseAdapter);

        Intent intent = getIntent();
        studentId = resolveStudentId(intent);
        if (saveButton != null) {
            saveButtonOriginalText = saveButton.getText();
        }

        setupViewModelObservers();

        bindRoutine(loadRoutine());

        addExerciseButton.setOnClickListener(v -> {
            exerciseAdapter.addExercise(new EditableExercise());
        });

        saveButton.setOnClickListener(v -> attemptRoutineCreation());

        homeButton.setOnClickListener(v -> {
            Intent homeIntent = new Intent(RutinaEntrenadorActivity.this, InicioEntrenadorActivity.class);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(homeIntent);
        });

        profileButton.setOnClickListener(v ->
                startActivity(new Intent(RutinaEntrenadorActivity.this, PerfilEntrenadorActivity.class)));
    }

    private void setupViewModelObservers() {
        viewModel.getIsSaving().observe(this, isSaving -> {
            setSavingState(isSaving);
        });

        viewModel.getSuccessMessage().observe(this, message -> {
            String routineName = routineNameEditText.getText().toString();
            if (TextUtils.isEmpty(routineName)) routineName = getString(R.string.trainer_routine_placeholder);

            Toast.makeText(this, getString(R.string.trainer_routine_create_success, routineName), Toast.LENGTH_LONG).show();
            finish();
        });

        viewModel.getErrorMessage().observe(this, error -> {
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

        return new RoutineRepository().getRoutineById(this, routineId);
    }

    private List<Long> buildExerciseIds() {
        List<EditableExercise> editableExercises = exerciseAdapter.getExercisesSnapshot();
        List<Long> exerciseIds = new ArrayList<>(editableExercises.size());
        for (EditableExercise editableExercise : editableExercises) {
            if (editableExercise == null) {
                continue;
            }
            long computedId = Math.abs((long) editableExercise.getId().hashCode());
            if (computedId == 0) {
                computedId = exerciseIds.size() + 1L;
            }
            exerciseIds.add(computedId);
        }
        return exerciseIds;
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

    private void attemptRoutineCreation() {
        if (Boolean.TRUE.equals(viewModel.getIsSaving().getValue())) {
            return;
        }

        String resolvedStudentId = studentId;

        if (TextUtils.isEmpty(resolvedStudentId)) {
            Toast.makeText(this,
                    R.string.trainer_routine_create_error_missing_student,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String routineName = routineNameEditText.getText() == null
                ? "" : routineNameEditText.getText().toString().trim();
        if (TextUtils.isEmpty(routineName)) {
            Toast.makeText(this,
                    R.string.trainer_routine_create_error_invalid_name,
                    Toast.LENGTH_SHORT).show();
            routineNameEditText.requestFocus();
            return;
        }

        String selectedDay = resolveSelectedDay();
        if (TextUtils.isEmpty(selectedDay)) {
            Toast.makeText(this,
                    R.string.trainer_routine_create_error_generic,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Long studentIdLong;
        try {
            studentIdLong = Long.parseLong(resolvedStudentId);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Error: ID de estudiante inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Long> exerciseIdsList = buildExerciseIds();
        java.util.Set<Long> exerciseIdsSet = new java.util.HashSet<>(exerciseIdsList);

        CreateRoutineRequest request = new CreateRoutineRequest(
                routineName,
                selectedDay,
                buildCurrentDate(),
                studentIdLong,
                exerciseIdsSet
        );

        viewModel.createRoutine(request);
    }

    private void setSavingState(boolean saving) {
        if (saveButton == null) {
            return;
        }
        saveButton.setEnabled(!saving);
        if (saving) {
            saveButton.setText(R.string.trainer_routine_saving);
        } else if (saveButtonOriginalText != null) {
            saveButton.setText(saveButtonOriginalText);
        }
    }

    @Nullable
    private String resolveSelectedDay() {
        if (routineDaySpinner == null || routineDaySpinner.getSelectedItem() == null) {
            return null;
        }
        String selectedDay = routineDaySpinner.getSelectedItem().toString();
        if (TextUtils.isEmpty(selectedDay)) {
            return null;
        }
        String normalized = selectedDay.trim().toLowerCase(Locale.ROOT);
        switch (normalized) {
            case "lunes":
                return "MONDAY";
            case "martes":
                return "TUESDAY";
            case "miércoles":
            case "miercoles":
                return "WEDNESDAY";
            case "jueves":
                return "THURSDAY";
            case "viernes":
                return "FRIDAY";
            case "sábado":
            case "sabado":
                return "SATURDAY";
            case "domingo":
                return "SUNDAY";
            default:
                return selectedDay.trim().toUpperCase(Locale.ROOT);
        }
    }

    private String buildCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        return dateFormat.format(new Date());
    }

    @Nullable
    private String resolveStudentId(@Nullable Intent intent) {
        if (intent == null) {
            return null;
        }
        String id = intent.getStringExtra(EXTRA_STUDENT_ID);
        if (TextUtils.isEmpty(id)) {
            id = intent.getStringExtra(RutinasEntrenadorActivity.EXTRA_STUDENT_ID);
        }
        return id;
    }
}