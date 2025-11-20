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
import com.example.gym_app.data.exercises.ExerciseRepository;
import com.example.gym_app.data.exercises.ExerciseRequest;
import com.example.gym_app.data.routines.CreateRoutineRequest;
import com.example.gym_app.data.RoutineRepository;
import com.example.gym_app.data.routines.CreateRoutineRequest;
import com.example.gym_app.data.routines.UpdateRoutineRequest;
import com.example.gym_app.model.EditableExercise;
import com.example.gym_app.model.Exercise;
import com.example.gym_app.model.Routine;
import com.example.gym_app.model.WeightType;
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

    private final RoutineRepository routineRepository = new RoutineRepository();
    private final ExerciseRepository exerciseRepository = new ExerciseRepository();
    private final Set<Long> loadedExerciseIds = new HashSet<>();
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

    @Override
    protected void onDestroy() {
        routineRepository.cancelRoutineCreation();
        exerciseRepository.cancelOngoingCall();
        super.onDestroy();
    }

    private void loadRoutineFromApi(Long routineId) {
        showLoading(true);

        routineRepository.getRoutineById(this, routineId, new RoutineRepository.GetRoutineCallback() {
            @Override
            public void onSuccess(Routine routine) {
                showLoading(false);
                bindRoutine(routine);
                if (routine != null && routine.getId() != null) {
                    try {
                        Long routineIdLong = Long.parseLong(routine.getId());
                        loadExercisesFromApi(routineIdLong);
                    } catch (NumberFormatException ignored) {
                    }
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
        updateLoadedExerciseIds(exerciseAdapter.getExercisesSnapshot());
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

    private Set<Long> buildExerciseIdsSet() {
        List<EditableExercise> editableExercises = exerciseAdapter.getExercisesSnapshot();
        Set<Long> exerciseIds = new HashSet<>();
        for (EditableExercise editableExercise : editableExercises) {
            if (editableExercise != null && editableExercise.getBackendId() != null) {
                exerciseIds.add(editableExercise.getBackendId());
            }
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

    private void loadExercisesFromApi(Long routineId) {
        showLoading(true);
        exerciseRepository.getExercisesByRoutine(this, routineId, new ExerciseRepository.GetExercisesCallback() {
            @Override
            public void onSuccess(List<Exercise> exercises) {
                showLoading(false);
                List<EditableExercise> editableExercises = mapExercises(exercises);
                exerciseAdapter.setExercises(editableExercises);
                updateLoadedExerciseIds(editableExercises);
                updateEmptyState(exerciseAdapter.getItemCount());
            }

            @Override
            public void onError(@NonNull String errorMessage) {
                showLoading(false);
                Toast.makeText(RutinaEntrenadorActivity.this,
                        errorMessage,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateLoadedExerciseIds(@Nullable List<EditableExercise> editableExercises) {
        loadedExerciseIds.clear();
        if (editableExercises == null) {
            return;
        }
        for (EditableExercise editableExercise : editableExercises) {
            if (editableExercise != null && editableExercise.getBackendId() != null) {
                loadedExerciseIds.add(editableExercise.getBackendId());
            }
        }
    }

    @Nullable
    private List<ExercisePayload> buildExercisePayloads(@Nullable Long routineId) {
        List<EditableExercise> editableExercises = exerciseAdapter.getExercisesSnapshot();
        List<ExercisePayload> payloads = new ArrayList<>(editableExercises.size());

        for (int index = 0; index < editableExercises.size(); index++) {
            EditableExercise editableExercise = editableExercises.get(index);
            if (editableExercise == null) {
                continue;
            }
            int displayIndex = index + 1;
            String exerciseName = editableExercise.getName() == null
                    ? "" : editableExercise.getName().trim();
            if (TextUtils.isEmpty(exerciseName)) {
                showExerciseError(getString(R.string.trainer_exercise_error_invalid_name, displayIndex));
                return null;
            }

            Integer sets = parsePositiveInt(editableExercise.getSeries());
            if (sets == null) {
                showExerciseError(getString(R.string.trainer_exercise_error_invalid_sets, displayIndex));
                return null;
            }

            Integer repetitions = parsePositiveInt(editableExercise.getRepetitions());
            if (repetitions == null) {
                showExerciseError(getString(R.string.trainer_exercise_error_invalid_repetitions, displayIndex));
                return null;
            }

            Integer restTime = parseNonNegativeInt(editableExercise.getRest());
            if (restTime == null) {
                showExerciseError(getString(R.string.trainer_exercise_error_invalid_rest, displayIndex));
                return null;
            }

            String weightType = editableExercise.getWeightType();
            if (TextUtils.isEmpty(weightType)) {
                showExerciseError(getString(R.string.trainer_exercise_error_invalid_weight_type, displayIndex));
                return null;
            }

            ExerciseRequest request = new ExerciseRequest();
            request.setName(exerciseName);
            request.setSets(sets);
            request.setRepetitions(repetitions);
            request.setRestTime(restTime);
            request.setWeightType(WeightType.toApiValue(weightType));
            if (routineId != null) {
                request.setRoutineId(routineId);
            }

            payloads.add(new ExercisePayload(editableExercise, request));
        }
        return payloads;
    }

    private Integer parsePositiveInt(String value) {
        if (TextUtils.isEmpty(value)) {
            return null;
        }
        try {
            int parsed = Integer.parseInt(value.trim());
            return parsed >= 1 ? parsed : null;
        } catch (Exception exception) {
            return null;
        }
    }

    private Integer parseNonNegativeInt(String value) {
        if (TextUtils.isEmpty(value)) {
            return 0;
        }
        try {
            int parsed = Integer.parseInt(value.trim());
            return parsed >= 0 ? parsed : null;
        } catch (Exception exception) {
            return null;
        }
    }

    private void showExerciseError(@NonNull String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private Set<Long> extractBackendExerciseIds(List<ExercisePayload> payloads) {
        Set<Long> ids = new HashSet<>();
        if (payloads == null) {
            return ids;
        }
        for (ExercisePayload payload : payloads) {
            if (payload != null && payload.source.getBackendId() != null) {
                ids.add(payload.source.getBackendId());
            }
        }
        return ids;
    }

    private void handleExercisePersistenceAfterRoutineCreation(String routineName,
                                                               @Nullable Routine routine,
                                                               List<ExercisePayload> payloads) {
        Long routineId = parseRoutineId(routine);
        if (routineId == null) {
            setSavingState(false);
            Toast.makeText(this,
                    R.string.trainer_routine_create_error_generic,
                    Toast.LENGTH_LONG).show();
            return;
        }
        persistExercises(routineId, payloads, () -> {
            setSavingState(false);
            String displayName = TextUtils.isEmpty(routineName)
                    ? getString(R.string.trainer_routine_placeholder)
                    : routineName;
            Toast.makeText(RutinaEntrenadorActivity.this,
                    getString(R.string.trainer_routine_create_success, displayName),
                    Toast.LENGTH_LONG).show();
            setResult(RESULT_OK);
            finish();
        }, errorMessage -> {
            setSavingState(false);
            Toast.makeText(RutinaEntrenadorActivity.this,
                    errorMessage,
                    Toast.LENGTH_LONG).show();
        });
    }

    @Nullable
    private Long parseRoutineId(@Nullable Routine routine) {
        if (routine == null || routine.getId() == null) {
            return null;
        }
        try {
            return Long.parseLong(routine.getId());
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private void persistExercises(Long routineId,
                                  List<ExercisePayload> payloads,
                                  Runnable onSuccess,
                                  ErrorCallback onError) {
        if (payloads == null) {
            if (onError != null) {
                onError.onError(getString(R.string.trainer_routine_create_error_generic));
            }
            return;
        }

        for (ExercisePayload payload : payloads) {
            if (payload != null && payload.request.getRoutineId() == null) {
                payload.request.setRoutineId(routineId);
            }
        }

        Set<Long> idsToDelete = new HashSet<>(loadedExerciseIds);
        for (ExercisePayload payload : payloads) {
            if (payload != null && payload.source.getBackendId() != null) {
                idsToDelete.remove(payload.source.getBackendId());
            }
        }

        int totalOperations = idsToDelete.size() + payloads.size();
        if (totalOperations == 0) {
            if (onSuccess != null) {
                onSuccess.run();
            }
            return;
        }

        final int[] completed = {0};
        final boolean[] hasError = {false};

        for (ExercisePayload payload : payloads) {
            if (payload == null) {
                handleExerciseOperationCompletion(totalOperations, completed, hasError, onSuccess);
                continue;
            }
            ExerciseRequest request = payload.request;
            if (request.getRoutineId() == null) {
                request.setRoutineId(routineId);
            }
            Long backendId = payload.source.getBackendId();
            if (backendId == null) {
                exerciseRepository.createExercise(this, request, new ExerciseRepository.ExerciseMutationCallback() {
                    @Override
                    public void onSuccess(@Nullable Exercise exercise) {
                        if (exercise != null) {
                            payload.source.setBackendId(exercise.getId());
                            loadedExerciseIds.add(exercise.getId());
                        }
                        handleExerciseOperationCompletion(totalOperations, completed, hasError, onSuccess);
                    }

                    @Override
                    public void onError(@NonNull String errorMessage) {
                        handleExerciseOperationError(hasError, onError, errorMessage);
                    }
                });
            } else {
                exerciseRepository.updateExercise(this, backendId, request,
                        new ExerciseRepository.ExerciseMutationCallback() {
                            @Override
                            public void onSuccess(@Nullable Exercise exercise) {
                                if (exercise != null && exercise.getId() != null) {
                                    payload.source.setBackendId(exercise.getId());
                                    loadedExerciseIds.add(exercise.getId());
                                }
                                handleExerciseOperationCompletion(totalOperations, completed, hasError, onSuccess);
                            }

                            @Override
                            public void onError(@NonNull String errorMessage) {
                                handleExerciseOperationError(hasError, onError, errorMessage);
                            }
                        });
            }
        }

        for (Long exerciseId : idsToDelete) {
            exerciseRepository.deleteExercise(this, exerciseId, new ExerciseRepository.DeleteExerciseCallback() {
                @Override
                public void onSuccess() {
                    loadedExerciseIds.remove(exerciseId);
                    handleExerciseOperationCompletion(totalOperations, completed, hasError, onSuccess);
                }

                @Override
                public void onError(@NonNull String errorMessage) {
                    handleExerciseOperationError(hasError, onError, errorMessage);
                }
            });
        }
    }

    private void handleExerciseOperationCompletion(int totalOperations,
                                                   int[] completed,
                                                   boolean[] hasError,
                                                   Runnable onSuccess) {
        if (hasError[0]) {
            return;
        }
        completed[0] += 1;
        if (completed[0] >= totalOperations) {
            updateLoadedExerciseIds(exerciseAdapter.getExercisesSnapshot());
            if (onSuccess != null) {
                Toast.makeText(this,
                        R.string.trainer_exercise_save_success,
                        Toast.LENGTH_SHORT).show();
                onSuccess.run();
            }
        }
    }

    private void handleExerciseOperationError(boolean[] hasError,
                                              ErrorCallback onError,
                                              String errorMessage) {
        if (hasError[0]) {
            return;
        }
        hasError[0] = true;
        if (onError != null) {
            String resolvedError = TextUtils.isEmpty(errorMessage)
                    ? getString(R.string.trainer_routine_create_error_generic)
                    : errorMessage;
            onError.onError(resolvedError);
        }
    }

    private interface ErrorCallback {
        void onError(@NonNull String message);
    }

    private static class ExercisePayload {
        private final EditableExercise source;
        private final ExerciseRequest request;

        ExercisePayload(EditableExercise source, ExerciseRequest request) {
            this.source = source;
            this.request = request;
        }
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

        List<ExercisePayload> exercisePayloads = buildExercisePayloads(null);
        if (exercisePayloads == null) {
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
            public void onSuccess(@Nullable Routine routine) {
                handleExercisePersistenceAfterRoutineCreation(routineName, routine, exercisePayloads);
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

        List<ExercisePayload> exercisePayloads = buildExercisePayloads(routineIdToEdit);
        if (exercisePayloads == null) {
            return;
        }

        UpdateRoutineRequest request = new UpdateRoutineRequest();
        request.setName(routineName);
        request.setDayOfWeek(selectedDay);
        request.setDate(buildCurrentDate());
        request.setExerciseIds(extractBackendExerciseIds(exercisePayloads));

        if (!TextUtils.isEmpty(studentId)) {
            request.setStudentId(Long.parseLong(studentId));
        }

        setSavingState(true);
        routineRepository.updateRoutine(this, routineIdToEdit, request,
                new RoutineRepository.UpdateRoutineCallback() {
                    @Override
                    public void onSuccess() {
                        persistExercises(routineIdToEdit, exercisePayloads, () -> {
                            setSavingState(false);
                            Toast.makeText(RutinaEntrenadorActivity.this,
                                    getString(R.string.trainer_routine_update_success, routineName),
                                    Toast.LENGTH_LONG).show();
                            setResult(RESULT_OK);
                            finish();
                        }, errorMessage -> {
                            setSavingState(false);
                            Toast.makeText(RutinaEntrenadorActivity.this,
                                    errorMessage,
                                    Toast.LENGTH_LONG).show();
                        });

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