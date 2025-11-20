package com.example.gym_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gym_app.adapter.TrainerStudentsAdapter;
import com.example.gym_app.data.RoutineRepository;
import com.example.gym_app.data.UserRepository;
import com.example.gym_app.data.auth.AuthRepository;
import com.example.gym_app.data.auth.SavedLoginData;
import com.example.gym_app.data.users.UserResponse;
import com.example.gym_app.model.Routine;
import com.example.gym_app.model.TrainerStudent;

import java.util.ArrayList;
import java.util.List;

public class InicioEntrenadorActivity extends AppCompatActivity {

    private TrainerStudentsAdapter adapter;
    private List<TrainerStudent> studentList = new ArrayList<>();

    private RoutineRepository routineRepository;
    private UserRepository userRepository;
    private AuthRepository authRepository;

    private ProgressBar loadingIndicator;

    private String currentTrainerGym = "SportClub";
    private String currentTrainerName = "Entrenador Actual";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_entrenador);

        routineRepository = new RoutineRepository();
        userRepository = new UserRepository();
        authRepository = new AuthRepository();

        SavedLoginData savedData = authRepository.getSavedLoginData(this);
        if (savedData != null) {
            currentTrainerName = savedData.getDisplayName();
            String savedGym = savedData.getGymName();
            if (savedGym != null && !savedGym.isEmpty()) {
                currentTrainerGym = savedGym;
            }
        }

        TextView trainerNameTextView = findViewById(R.id.trainerName);
        RecyclerView studentsRecyclerView = findViewById(R.id.recycler_students);
        LinearLayout profileButton = findViewById(R.id.nav_profile);

        loadingIndicator = findViewById(R.id.progress_bar);

        trainerNameTextView.setText(currentTrainerName);
        studentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TrainerStudentsAdapter(studentList, new TrainerStudentsAdapter.OnStudentClickListener() {
            @Override
            public void onStudentSelected(TrainerStudent student) {
                fetchRoutinesAndNavigate(student);
            }
        });

        studentsRecyclerView.setAdapter(adapter);

        profileButton.setOnClickListener(v ->
                startActivity(new Intent(InicioEntrenadorActivity.this, PerfilEntrenadorActivity.class)));

        loadStudentsFromApi();
    }

    private void loadStudentsFromApi() {
        if (loadingIndicator != null) loadingIndicator.setVisibility(View.VISIBLE);

        userRepository.getAllUsers(this, new UserRepository.GetAllUsersCallback() {
            @Override
            public void onSuccess(List<UserResponse> users) {
                if (loadingIndicator != null) loadingIndicator.setVisibility(View.GONE);
                filterAndDisplayStudents(users);
            }

            @Override
            public void onError(String errorMessage) {
                if (loadingIndicator != null) loadingIndicator.setVisibility(View.GONE);
                Toast.makeText(InicioEntrenadorActivity.this,
                        "Error al cargar alumnos: " + errorMessage,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterAndDisplayStudents(List<UserResponse> allUsers) {
        studentList.clear();

        if (allUsers != null) {
            for (UserResponse user : allUsers) {
                String userRoleStr = String.valueOf(user.getRole());
                String userGymStr = user.getGymName();

                android.util.Log.d("FILTRO_DEBUG", "Analizando: " + user.getName() +
                        " | Rol API: " + userRoleStr +
                        " | Gym API: " + userGymStr);

                boolean isStudent = "STUDENT".equalsIgnoreCase(userRoleStr);
                boolean isSameGym = currentTrainerGym.equalsIgnoreCase(userGymStr);

                if (isStudent && isSameGym) {
                    android.util.Log.d("FILTRO_DEBUG", ">>> ACEPTADO: " + user.getName());

                    studentList.add(new TrainerStudent(
                            String.valueOf(user.getId()),
                            user.getName(),
                            new ArrayList<>()
                    ));
                }
            }
        }

        if (adapter != null) {
            adapter.setStudents(studentList);
        }
    }

    public void fetchRoutinesAndNavigate(TrainerStudent student) {
        if (loadingIndicator != null) loadingIndicator.setVisibility(View.VISIBLE);

        Long studentId = Long.parseLong(student.getId());

        routineRepository.getRoutinesByUserId(this, studentId, new RoutineRepository.GetAllRoutinesCallback() {
            @Override
            public void onSuccess(List<Routine> routines) {
                if (loadingIndicator != null) loadingIndicator.setVisibility(View.GONE);

                ArrayList<String> routineIds = new ArrayList<>();
                if (routines != null) {
                    for (Routine routine : routines) {
                        routineIds.add(routine.getId());
                    }
                }

                Intent intent = new Intent(InicioEntrenadorActivity.this, RutinasEntrenadorActivity.class);
                intent.putExtra(RutinasEntrenadorActivity.EXTRA_STUDENT_ID, student.getId());
                intent.putExtra(RutinasEntrenadorActivity.EXTRA_STUDENT_NAME, student.getFullName());
                intent.putStringArrayListExtra(RutinasEntrenadorActivity.EXTRA_STUDENT_ROUTINE_IDS, routineIds);

                startActivity(intent);
            }

            @Override
            public void onError(String errorMessage) {
                if (loadingIndicator != null) loadingIndicator.setVisibility(View.GONE);
                Toast.makeText(InicioEntrenadorActivity.this, "Error cargando rutinas: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }
}
