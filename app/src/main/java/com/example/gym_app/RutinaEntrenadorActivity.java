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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gym_app.adapter.EditableExerciseAdapter;
import com.example.gym_app.data.RoutineRepository;
import com.example.gym_app.data.routines.CreateRoutineRequest;
import com.example.gym_app.data.routines.UpdateRoutineRequest;
import com.example.gym_app.model.EditableExercise;
import com.example.gym_app.model.Exercise;
import com.example.gym_app.model.Routine;
import com.example.gym_app.viewmodel.CreateRoutineViewModel;

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

    private CreateRoutineViewModel viewModel;

    private EditableExerciseAdapter exerciseAdapter;
    private EditText routineNameEditText;
    private Spinner routineDaySpinner;
    private TextView emptyStateTextView;
    private TextView exercisesHeaderTextView;
    private Button saveButton;
    private ProgressBar progressBar;
    private CharSequence saveButtonOriginalText;

    private boolean isEditMode = false;
    @Nullable
    private Long routineIdToEdit;

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

        setupViewModelObservers();

        String routineIdStr = intent != null ? intent.getStringExtra(EXTRA_ROUTINE_ID) : null;
        if (!TextUtils.isEmpty(routineIdStr)) {
            isEditMode = true;
            saveButton.setText("Actualizar Rutina");
            try {
                routineIdToEdit = Long.parseLong(routineIdStr);
                Routine routine = new RoutineRepository().getRoutineById(this, routineIdStr);
                bindRoutine(routine);
            } catch (NumberFormatException e) {
                bindRoutine(null);
            }
        } else {
            bindRoutine(null);
        }

        addExerciseButton.setOnClickListener(v -> {
            exerciseAdapter.addExercise(new EditableExercise());
        });

        saveButton.setOnClickListener(v -> {
            if (isEditMode && routineIdToEdit != null) {
                attemptRoutineUpdate(); // Método nuevo
            } else {
                attemptRoutineCreation(); // Método existente
            }
        });

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
            if (progressBar != null) {
                progressBar.setVisibility(isSaving ? View.VISIBLE : View.GONE);
            }
        });

        viewModel.getSuccessMessage().observe(this, message -> {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            finish();
        });

        viewModel.getErrorMessage().observe(this, error -> {
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        });
    }

    private void bindRoutine(@Nullable Routine routine) {
        if (routine != null) {
            routineNameEditText.setText(routine.getName());
            selectDayInSpinner(routine.getDayOfWeek());
            exerciseAdapter.setExercises(mapExercises(routine.getExercises()));
        }
        updateEmptyState(exerciseAdapter.getItemCount());
    }

    private void attemptRoutineCreation() {
        if (Boolean.TRUE.equals(viewModel.getIsSaving().getValue())) return;
        if (!validateInputs()) return;

        String selectedDay = resolveSelectedDay();
        List<Long> exerciseIds = buildExerciseIds();
        Long sId = Long.parseLong(studentId);

        CreateRoutineRequest request = new CreateRoutineRequest(
                routineNameEditText.getText().toString(),
                selectedDay,
                buildCurrentDate(),
                sId,
                new HashSet<>(exerciseIds)
        );

        viewModel.createRoutine(request);
    }

    private void attemptRoutineUpdate() {
        if (Boolean.TRUE.equals(viewModel.getIsSaving().getValue())) return;
        if (!validateInputs()) return;

        UpdateRoutineRequest request = new UpdateRoutineRequest();
        request.setName(routineNameEditText.getText().toString());
        request.setDayOfWeek(resolveSelectedDay());
        request.setDate(buildCurrentDate());
        request.setExerciseIds(new HashSet<>(buildExerciseIds()));

        if (studentId != null) {
            request.setStudentId(Long.parseLong(studentId));
        }

        viewModel.updateRoutine(routineIdToEdit, request);
    }

    private boolean validateInputs() {
        if (TextUtils.isEmpty(studentId)) {
            Toast.makeText(this, "Error: Falta el alumno", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(routineNameEditText.getText())) {
            routineNameEditText.setError("Requerido");
            return false;
        }
        if (TextUtils.isEmpty(resolveSelectedDay())) {
            Toast.makeText(this, "Selecciona un día", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    private List<Long> buildExerciseIds() {
        List<EditableExercise> editableExercises = exerciseAdapter.getExercisesSnapshot();
        List<Long> exerciseIds = new ArrayList<>(editableExercises.size());
        for (EditableExercise editableExercise : editableExercises) {
            long id = Math.abs((long) editableExercise.getId().hashCode());
            if (id == 0) id = exerciseIds.size() + 1L;
            exerciseIds.add(id);
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

    private void updateEmptyState(int itemCount) {
        if (emptyStateTextView != null) {
            emptyStateTextView.setVisibility(itemCount == 0 ? View.VISIBLE : View.GONE);
        }
        if (exercisesHeaderTextView != null) {
            exercisesHeaderTextView.setText(getResources().getQuantityString(R.plurals.trainer_exercises_header, itemCount, itemCount));
        }
    }

    private void setSavingState(boolean saving) {
        if (saveButton != null) {
            saveButton.setEnabled(!saving);
            saveButton.setText(saving ? "Guardando..." : (isEditMode ? "Actualizar Rutina" : saveButtonOriginalText));
        }
    }

    @Nullable
    private String resolveSelectedDay() {
        if (routineDaySpinner == null || routineDaySpinner.getSelectedItem() == null) return null;
        String selectedDay = routineDaySpinner.getSelectedItem().toString();
        return selectedDay.toUpperCase(Locale.ROOT).replace("Á", "A").replace("É", "E");
    }

    private String buildCurrentDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
    }

    @Nullable
    private String resolveStudentId(@Nullable Intent intent) {
        if (intent == null) return null;
        String id = intent.getStringExtra(EXTRA_STUDENT_ID);
        if (TextUtils.isEmpty(id)) id = intent.getStringExtra(EXTRA_STUDENT_ID);
        return id;
    }

    private void selectDayInSpinner(@Nullable String day) {
        if (routineDaySpinner == null || TextUtils.isEmpty(day)) return;
        String[] days = getResources().getStringArray(R.array.routine_days);
        for (int i = 0; i < days.length; i++) {
            if (day.equalsIgnoreCase(days[i])) {
                routineDaySpinner.setSelection(i);
                return;
            }
        }
    }
}