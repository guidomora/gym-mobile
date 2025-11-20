package com.example.gym_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gym_app.adapter.TrainerRoutineAdapter;
import com.example.gym_app.data.TrainerDashboardLocalDataSource;
import com.example.gym_app.model.Routine;
import com.example.gym_app.model.TrainerStudent;
import com.example.gym_app.viewmodel.TrainerViewModel;

import java.util.ArrayList;

public class RutinasEntrenadorActivity extends AppCompatActivity {

    public static final String EXTRA_STUDENT_ID = "extra_student_id";
    public static final String EXTRA_STUDENT_NAME = "extra_student_name";
    public static final String EXTRA_STUDENT_ROUTINE_IDS = "extra_student_routine_ids";

    private TrainerViewModel viewModel;
    private TrainerRoutineAdapter routineAdapter;
    private TextView emptyStateTextView;

    private String studentId;
    private ArrayList<String> studentRoutineIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rutinas_entrenador);

        viewModel = new ViewModelProvider(this).get(TrainerViewModel.class);

        ImageButton backButton = findViewById(R.id.btn_back);
        Button addRoutineButton = findViewById(R.id.btn_add_routine);
        LinearLayout homeButton = findViewById(R.id.nav_home);
        LinearLayout profileButton = findViewById(R.id.nav_profile);
        TextView studentNameTextView = findViewById(R.id.tv_student_name);
        emptyStateTextView = findViewById(R.id.tv_empty_state);
        RecyclerView routinesRecyclerView = findViewById(R.id.rv_student_routines);

        routinesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        routinesRecyclerView.setHasFixedSize(true);

        routineAdapter = new TrainerRoutineAdapter(new TrainerRoutineAdapter.OnRoutineActionListener() {
            @Override
            public void onRoutineSelected(Routine routine) {
                navigateToRoutineEditor(routine);
            }

            @Override
            public void onRoutineDeleted(Routine routine) {
                viewModel.deleteRoutine(routine);
            }
        });
        routinesRecyclerView.setAdapter(routineAdapter);

        processIntentData();

        if (studentNameTextView != null) {
            String displayName = getIntent().getStringExtra(EXTRA_STUDENT_NAME);
            if (TextUtils.isEmpty(displayName)) displayName = "Alumno";
            studentNameTextView.setText(getString(R.string.trainer_student_label, displayName));
        }

        observeViewModel();

        viewModel.loadRoutines(studentRoutineIds);

        backButton.setOnClickListener(v -> finish());
        addRoutineButton.setOnClickListener(v -> navigateToRoutineEditor(null));

        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, InicioEntrenadorActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        profileButton.setOnClickListener(v ->
                startActivity(new Intent(this, PerfilEntrenadorActivity.class)));
    }

    private void observeViewModel() {
        viewModel.getRoutinesState().observe(this, routines -> {
            routineAdapter.submitList(new ArrayList<>(routines));
        });

        viewModel.getEmptyState().observe(this, isEmpty -> {
            if (emptyStateTextView != null) {
                emptyStateTextView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            }
        });

        viewModel.getErrorState().observe(this, errorMsg -> {
            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
        });

        viewModel.getDeleteMessageState().observe(this, msg -> {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });
    }

    private void processIntentData() {
        studentId = getIntent().getStringExtra(EXTRA_STUDENT_ID);
        String studentName = getIntent().getStringExtra(EXTRA_STUDENT_NAME);
        ArrayList<String> routineIds = getIntent().getStringArrayListExtra(EXTRA_STUDENT_ROUTINE_IDS);

        if ((studentName == null || studentName.isEmpty()) || routineIds == null) {
            TrainerDashboardLocalDataSource dashboardData = TrainerDashboardLocalDataSource.loadFromResource(this);
            TrainerStudent student = dashboardData.findStudentById(studentId);
            if (student != null) {
                if (routineIds == null) routineIds = new ArrayList<>(student.getRoutineIds());
            }
        }
        this.studentRoutineIds = routineIds;
    }

    private void navigateToRoutineEditor(@Nullable Routine routine) {
        Intent intent = new Intent(this, RutinaEntrenadorActivity.class);
        if (routine != null) {
            if (!TextUtils.isEmpty(routine.getId()))
                intent.putExtra(RutinaEntrenadorActivity.EXTRA_ROUTINE_ID, routine.getId());
            if (!TextUtils.isEmpty(routine.getName()))
                intent.putExtra(RutinaEntrenadorActivity.EXTRA_ROUTINE_NAME, routine.getName());
            if (!TextUtils.isEmpty(routine.getDayOfWeek()))
                intent.putExtra(RutinaEntrenadorActivity.EXTRA_ROUTINE_DAY, routine.getDayOfWeek());
        }
        if (!TextUtils.isEmpty(studentId)) {
            intent.putExtra(RutinaEntrenadorActivity.EXTRA_STUDENT_ID, studentId);
        }
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (viewModel != null && studentRoutineIds != null) {
            viewModel.loadRoutines(studentRoutineIds);
        }
    }
}