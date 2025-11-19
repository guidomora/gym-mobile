package com.example.gym_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.Nullable;

import com.example.gym_app.adapter.TrainerRoutineAdapter;
import com.example.gym_app.data.RoutineRepository;
import com.example.gym_app.data.TrainerDashboardLocalDataSource;
import com.example.gym_app.model.Routine;
import com.example.gym_app.model.TrainerStudent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RutinasEntrenadorActivity extends AppCompatActivity {

    public static final String EXTRA_STUDENT_ID = "extra_student_id";
    public static final String EXTRA_STUDENT_NAME = "extra_student_name";
    public static final String EXTRA_STUDENT_ROUTINE_IDS = "extra_student_routine_ids";

    private static final int REQUEST_CODE_CREATE_ROUTINE = 1001;
    private static final int REQUEST_CODE_EDIT_ROUTINE = 1002;

    private RoutineRepository routineRepository;
    private TrainerRoutineAdapter routineAdapter;
    private TextView emptyStateTextView;
    private ProgressBar progressBar;
    private RecyclerView routinesRecyclerView;
    private ArrayList<String> studentRoutineIds = new ArrayList<>();
    private List<Routine> currentRoutines = new ArrayList<>();
    @Nullable
    private String studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rutinas_entrenador);

        ImageButton backButton = findViewById(R.id.btn_back);
        Button addRoutineButton = findViewById(R.id.btn_add_routine);
        LinearLayout homeButton = findViewById(R.id.nav_home);
        LinearLayout profileButton = findViewById(R.id.nav_profile);
        TextView studentNameTextView = findViewById(R.id.tv_student_name);
        emptyStateTextView = findViewById(R.id.tv_empty_state);
        progressBar = findViewById(R.id.progress_bar);
        routinesRecyclerView = findViewById(R.id.rv_student_routines);

        routinesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        routinesRecyclerView.setHasFixedSize(true);
        routineRepository = new RoutineRepository();

        routineAdapter = new TrainerRoutineAdapter(new TrainerRoutineAdapter.OnRoutineActionListener() {
            @Override
            public void onRoutineSelected(Routine routine) {
                navigateToRoutineEditor(routine, REQUEST_CODE_EDIT_ROUTINE);
            }

            @Override
            public void onRoutineDeleted(Routine routine) {
                handleRoutineDeletion(routine);
            }
        });

        routinesRecyclerView.setAdapter(routineAdapter);

        studentId = getIntent().getStringExtra(EXTRA_STUDENT_ID);
        String studentName = getIntent().getStringExtra(EXTRA_STUDENT_NAME);
        ArrayList<String> routineIds = getIntent().getStringArrayListExtra(EXTRA_STUDENT_ROUTINE_IDS);

        if ((studentName == null || studentName.isEmpty()) || routineIds == null) {
            TrainerDashboardLocalDataSource dashboardDataSource = TrainerDashboardLocalDataSource.loadFromResource(this);
            TrainerStudent student = dashboardDataSource.findStudentById(studentId);
            if (student != null) {
                if (studentName == null || studentName.isEmpty()) {
                    studentName = student.getFullName();
                }
                if (routineIds == null) {
                    routineIds = new ArrayList<>(student.getRoutineIds());
                }
            }
        }

        if (routineIds != null) {
            studentRoutineIds = new ArrayList<>(routineIds);
        }

        if (studentName != null && !studentName.isEmpty()) {
            studentNameTextView.setText(getString(R.string.trainer_student_label, studentName));
        } else {
            studentNameTextView.setText(R.string.trainer_student_placeholder);
        }

        backButton.setOnClickListener(v -> finish());
        addRoutineButton.setOnClickListener(v -> navigateToRoutineEditor(null, REQUEST_CODE_CREATE_ROUTINE));

        homeButton.setOnClickListener(v ->
                startActivity(new Intent(RutinasEntrenadorActivity.this, InicioEntrenadorActivity.class)));

        profileButton.setOnClickListener(v ->
                startActivity(new Intent(RutinasEntrenadorActivity.this, PerfilEntrenadorActivity.class)));

        loadRoutinesFromApi();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == REQUEST_CODE_CREATE_ROUTINE || requestCode == REQUEST_CODE_EDIT_ROUTINE)
                && resultCode == RESULT_OK) {
            loadRoutinesFromApi();
        }
    }

    private void loadRoutinesFromApi() {
        showLoading(true);

        routineRepository.getAllRoutines(this, new RoutineRepository.GetAllRoutinesCallback() {
            @Override
            public void onSuccess(List<Routine> routines) {
                showLoading(false);

                currentRoutines = new ArrayList<>(routines);
                routineAdapter.submitList(new ArrayList<>(currentRoutines));
                updateEmptyState(currentRoutines.isEmpty());
            }

            @Override
            public void onError(@NonNull String errorMessage) {
                showLoading(false);
                Toast.makeText(RutinasEntrenadorActivity.this,
                        errorMessage,
                        Toast.LENGTH_SHORT).show();
                loadLocalRoutines();
            }
        });
    }

    private void loadLocalRoutines() {
        currentRoutines = loadStudentRoutines(studentRoutineIds);
        routineAdapter.submitList(new ArrayList<>(currentRoutines));
        updateEmptyState(currentRoutines.isEmpty());
    }

    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (routinesRecyclerView != null) {
            routinesRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void navigateToRoutineEditor(@Nullable Routine routine, int requestCode) {
        Intent intent = new Intent(RutinasEntrenadorActivity.this, RutinaEntrenadorActivity.class);

        if (routine != null) {
            if (!TextUtils.isEmpty(routine.getId())) {
                intent.putExtra(RutinaEntrenadorActivity.EXTRA_ROUTINE_ID, routine.getId());
            }
            if (!TextUtils.isEmpty(routine.getName())) {
                intent.putExtra(RutinaEntrenadorActivity.EXTRA_ROUTINE_NAME, routine.getName());
            }
            if (!TextUtils.isEmpty(routine.getDayOfWeek())) {
                intent.putExtra(RutinaEntrenadorActivity.EXTRA_ROUTINE_DAY, routine.getDayOfWeek());
            }
        }

        if (!TextUtils.isEmpty(studentId)) {
            intent.putExtra(RutinaEntrenadorActivity.EXTRA_STUDENT_ID, studentId);
        }

        startActivityForResult(intent, requestCode);
    }

    private List<Routine> loadStudentRoutines(@Nullable List<String> routineIds) {
        if (routineIds == null || routineIds.isEmpty()) {
            return Collections.emptyList();
        }
        return routineRepository.getRoutinesByIds(this, routineIds);
    }

    private void updateEmptyState(boolean isEmpty) {
        if (emptyStateTextView == null) {
            return;
        }
        emptyStateTextView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        if (routinesRecyclerView != null) {
            routinesRecyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        }
    }

    private void handleRoutineDeletion(Routine routine) {
        if (routine == null || TextUtils.isEmpty(routine.getId())) {
            return;
        }

        Long routineId;
        try {
            routineId = Long.parseLong(routine.getId());
        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.trainer_routine_delete_error, Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);

        routineRepository.deleteRoutine(this, routineId, new RoutineRepository.DeleteRoutineCallback() {
            @Override
            public void onSuccess() {
                showLoading(false);

                List<Routine> updatedRoutines = new ArrayList<>();
                for (Routine currentRoutine : currentRoutines) {
                    if (currentRoutine == null || TextUtils.isEmpty(currentRoutine.getId())) {
                        continue;
                    }
                    if (!TextUtils.equals(currentRoutine.getId(), routine.getId())) {
                        updatedRoutines.add(currentRoutine);
                    }
                }
                currentRoutines = updatedRoutines;
                routineAdapter.submitList(new ArrayList<>(currentRoutines));
                updateEmptyState(currentRoutines.isEmpty());

                if (studentRoutineIds != null) {
                    studentRoutineIds.remove(routine.getId());
                }

                String routineName = TextUtils.isEmpty(routine.getName())
                        ? getString(R.string.trainer_routine_placeholder)
                        : routine.getName();
                Toast.makeText(RutinasEntrenadorActivity.this,
                        getString(R.string.trainer_routine_deleted_message, routineName),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(@NonNull String errorMessage) {
                showLoading(false);
                Toast.makeText(RutinasEntrenadorActivity.this,
                        errorMessage,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        routineRepository.cancelRoutineCreation();
        super.onDestroy();
    }
}