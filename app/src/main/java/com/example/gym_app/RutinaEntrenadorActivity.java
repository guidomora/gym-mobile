package com.example.gym_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gym_app.adapter.EditableExerciseAdapter;
import com.example.gym_app.data.routines.CreateRoutineRequest;
import com.example.gym_app.data.RoutineRepository;
import com.example.gym_app.data.routines.UpdateRoutineRequest;
import com.example.gym_app.model.EditableExercise;
import com.example.gym_app.model.Exercise;
import com.example.gym_app.model.Routine;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class RutinaEntrenadorActivity extends AppCompatActivity {

    public static final String EXTRA_ROUTINE_ID = "extra_trainer_routine_id";
    public static final String EXTRA_ROUTINE_NAME = "extra_trainer_routine_name";
    public static final String EXTRA_ROUTINE_DAY = "extra_trainer_routine_day";
    public static final String EXTRA_STUDENT_ID = "extra_trainer_student_id";

    private final RoutineRepository routineRepository = new RoutineRepository();

    private EditableExerciseAdapter exerciseAdapter;
    private EditText routineNameEditText;
    private Spinner routineDaySpinner;
    private TextView emptyStateTextView;
    private TextView exercisesHeaderTextView;
    private Button saveButton;
    private ProgressBar progressBar;
    private CharSequence saveButtonOriginalText;
    private boolean isSavingRoutine;
    private boolean isEditMode = false;

    @Nullable
    private String studentId;
    @Nullable
    private Long routineIdToEdit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rutina_entrenador);

        routineNameEditText = findViewById(R.id.et_routine_name);
        routineDaySpinner = findViewById(R.id.spinner_days);
        emptyStateTextView = findViewById(R.id.tv_empty_state);
        exercisesHeaderTextView = findViewById(R.id.tv_exercises_title);
        progressBar = findViewById(R.id.progress_bar);
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

        String routineIdStr = intent != null ? intent.getStringExtra(EXTRA_ROUTINE_ID) : null;
        if (!TextUtils.isEmpty(routineIdStr)) {
            isEditMode = true;
            try {
                routineIdToEdit = Long.parseLong(routineIdStr);
                loadRoutineFromApi(routineIdToEdit);
            } catch (NumberFormatException e) {
                bindRoutine(loadRoutineLocal());
            }
        } else {
            bindRoutine(null);
        }

        addExerciseButton.setOnClickListener(v -> {
            exerciseAdapter.addExercise(new EditableExercise());
        });

        saveButton.setOnClickListener(v -> {
            if (isEditMode && routineIdToEdit != null) {
                attemptRoutineUpdate();
            } else {
                attemptRoutineCreation();
            }
        });

        homeButton.setOnClickListener(v ->
                startActivity(new Intent(RutinaEntrenadorActivity.this, InicioEntrenadorActivity.class)));

        profileButton.setOnClickListener(v ->
                startActivity(new Intent(RutinaEntrenadorActivity.this, PerfilEntrenadorActivity.class)));
    }

    @Override
    protected void onDestroy() {
        routineRepository.cancelRoutineCreation();
        super.onDestroy();
    }

    private void loadRoutineFromApi(Long routineId) {
        showLoading(true);

        routineRepository.getRoutineById(this, routineId, new RoutineRepository.GetRoutineCallback() {
            @Override
            public void onSuccess(Routine routine) {
                showLoading(false);
                bindRoutine(routine);
            }

            @Override
            public void onError(@NonNull String errorMessage) {
                showLoading(false);
                Toast.makeText(RutinaEntrenadorActivity.this,
                        errorMessage,
                        Toast.LENGTH_SHORT).show();
                bindRoutine(loadRoutineLocal());
            }
        });
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
    private Routine loadRoutineLocal() {
        Intent intent = getIntent();
        if (intent == null) {
            return null;
        }
        String routineId = intent.getStringExtra(EXTRA_ROUTINE_ID);
        if (TextUtils.isEmpty(routineId)) {
            return null;
        }
        return routineRepository.getRoutineById(this, routineId);
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

    private Set<Long> buildExerciseIdsSet() {
        return new HashSet<>(buildExerciseIds());
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
        if (isSavingRoutine) {
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

        Set<Long> exerciseIds = buildExerciseIdsSet();
        CreateRoutineRequest request = new CreateRoutineRequest(
                routineName,
                selectedDay,
                buildCurrentDate(),
                Long.parseLong(studentId),
                exerciseIds
        );

        setSavingState(true);
        routineRepository.createRoutine(this, request, new RoutineRepository.CreateRoutineCallback() {
            @Override
            public void onSuccess() {
                setSavingState(false);
                String displayName = routineName;
                if (TextUtils.isEmpty(displayName)) {
                    displayName = getString(R.string.trainer_routine_placeholder);
                }
                Toast.makeText(RutinaEntrenadorActivity.this,
                        getString(R.string.trainer_routine_create_success, displayName),
                        Toast.LENGTH_LONG).show();
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onError(@NonNull String errorMessage) {
                setSavingState(false);
                Toast.makeText(RutinaEntrenadorActivity.this,
                        errorMessage,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void attemptRoutineUpdate() {
        if (isSavingRoutine || routineIdToEdit == null) {
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

        UpdateRoutineRequest request = new UpdateRoutineRequest();
        request.setName(routineName);
        request.setDayOfWeek(selectedDay);
        request.setDate(buildCurrentDate());
        request.setExerciseIds(buildExerciseIdsSet());

        if (!TextUtils.isEmpty(studentId)) {
            request.setStudentId(Long.parseLong(studentId));
        }

        setSavingState(true);
        routineRepository.updateRoutine(this, routineIdToEdit, request,
                new RoutineRepository.UpdateRoutineCallback() {
                    @Override
                    public void onSuccess() {
                        setSavingState(false);
                        Toast.makeText(RutinaEntrenadorActivity.this,
                                getString(R.string.trainer_routine_update_success, routineName),
                                Toast.LENGTH_LONG).show();
                        setResult(RESULT_OK);
                        finish();

                    }

                    @Override
                    public void onError(@NonNull String errorMessage) {
                        setSavingState(false);
                        Toast.makeText(RutinaEntrenadorActivity.this,
                                errorMessage,
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (saveButton != null) {
            saveButton.setEnabled(!show);
        }
    }

    private void setSavingState(boolean saving) {
        isSavingRoutine = saving;
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