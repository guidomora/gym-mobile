package com.example.gym_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gym_app.adapter.TrainerRoutineAdapter;
import com.example.gym_app.data.RoutineLocalDataSource;
import com.example.gym_app.data.TrainerDashboardLocalDataSource;
import com.example.gym_app.model.Routine;
import com.example.gym_app.model.TrainerStudent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RutinasEntrenadorActivity extends AppCompatActivity {

    public static final String EXTRA_STUDENT_ID = "extra_student_id";
    public static final String EXTRA_STUDENT_NAME = "extra_student_name";
    public static final String EXTRA_STUDENT_ROUTINE_IDS = "extra_student_routine_ids";

    private TrainerRoutineAdapter routineAdapter;
    private TextView emptyStateTextView;

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
        RecyclerView routinesRecyclerView = findViewById(R.id.rv_student_routines);

        routinesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        routinesRecyclerView.setHasFixedSize(true);
        routineAdapter = new TrainerRoutineAdapter(routine -> {
            if (routine == null) {
                startActivity(new Intent(RutinasEntrenadorActivity.this, RutinaEntrenadorActivity.class));
                return;
            }
            Intent intent = new Intent(RutinasEntrenadorActivity.this, RutinaEntrenadorActivity.class);
            if (!TextUtils.isEmpty(routine.getId())) {
                intent.putExtra(RutinaEntrenadorActivity.EXTRA_ROUTINE_ID, routine.getId());
            }
            if (!TextUtils.isEmpty(routine.getName())) {
                intent.putExtra(RutinaEntrenadorActivity.EXTRA_ROUTINE_NAME, routine.getName());
            }
            if (!TextUtils.isEmpty(routine.getDayOfWeek())) {
                intent.putExtra(RutinaEntrenadorActivity.EXTRA_ROUTINE_DAY, routine.getDayOfWeek());
            }
            startActivity(intent);
        });

        routineAdapter = new TrainerRoutineAdapter(routine ->
                startActivity(new Intent(RutinasEntrenadorActivity.this, RutinaEntrenadorActivity.class)));
        routinesRecyclerView.setAdapter(routineAdapter);

        String studentId = getIntent().getStringExtra(EXTRA_STUDENT_ID);
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

        if (studentName != null && !studentName.isEmpty()) {
            studentNameTextView.setText(getString(R.string.trainer_student_label, studentName));
        } else {
            studentNameTextView.setText(R.string.trainer_student_placeholder);
        }

        List<Routine> routines = loadStudentRoutines(routineIds);
        routineAdapter.submitList(routines);
        updateEmptyState(routines.isEmpty());

        backButton.setOnClickListener(v -> finish());

        addRoutineButton.setOnClickListener(v ->
                startActivity(new Intent(RutinasEntrenadorActivity.this, RutinaEntrenadorActivity.class)));

        homeButton.setOnClickListener(v ->
                startActivity(new Intent(RutinasEntrenadorActivity.this, InicioEntrenadorActivity.class)));

        profileButton.setOnClickListener(v ->
                startActivity(new Intent(RutinasEntrenadorActivity.this, PerfilEntrenadorActivity.class)));
    }

    private List<Routine> loadStudentRoutines(@Nullable List<String> routineIds) {
        if (routineIds == null || routineIds.isEmpty()) {
            return Collections.emptyList();
        }

        RoutineLocalDataSource routineLocalDataSource = new RoutineLocalDataSource();
        List<Routine> allRoutines = routineLocalDataSource.getRoutines(this);
        if (allRoutines.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, Routine> routinesById = new HashMap<>();
        for (Routine routine : allRoutines) {
            String id = routine.getId();
            if (id != null && !id.isEmpty()) {
                routinesById.put(id, routine);
            }
        }

        List<Routine> studentRoutines = new ArrayList<>();
        for (String routineId : routineIds) {
            Routine routine = routinesById.get(routineId);
            if (routine != null) {
                studentRoutines.add(routine);
            }
        }

        return studentRoutines;
    }

    private void updateEmptyState(boolean isEmpty) {
        if (emptyStateTextView == null) {
            return;
        }
        emptyStateTextView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }
}