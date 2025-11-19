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
import com.example.gym_app.data.UserRepository; // Importante: Importar tu UserRepository
import com.example.gym_app.data.users.UserResponse;
import com.example.gym_app.model.Routine;
import com.example.gym_app.model.TrainerStudent;

import java.util.ArrayList;
import java.util.List;

public class InicioEntrenadorActivity extends AppCompatActivity {

    private TrainerStudentsAdapter adapter;
    private List<TrainerStudent> studentList = new ArrayList<>();

    // Repositorios
    private RoutineRepository routineRepository;
    private UserRepository userRepository; // 1. Agregamos el repositorio de usuarios

    // UI Loading
    private ProgressBar loadingIndicator;

    private String currentTrainerGym = "SportClub";
    private String currentTrainerName = "Entrenador Actual";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_entrenador);

        // Inicializar repositorios
        routineRepository = new RoutineRepository();
        userRepository = new UserRepository(); // 2. Inicializamos el repositorio

        TextView trainerNameTextView = findViewById(R.id.trainerName);
        RecyclerView studentsRecyclerView = findViewById(R.id.recycler_students);
        LinearLayout profileButton = findViewById(R.id.nav_profile);

        loadingIndicator = findViewById(R.id.progress_bar);

        trainerNameTextView.setText(currentTrainerName);
        studentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // CONFIGURAR ADAPTER
        adapter = new TrainerStudentsAdapter(studentList, new TrainerStudentsAdapter.OnStudentClickListener() {
            @Override
            public void onStudentSelected(TrainerStudent student) {
                // AL HACER CLICK, BUSCAMOS LAS RUTINAS DE ESE ALUMNO
                fetchRoutinesAndNavigate(student);
            }
        });

        studentsRecyclerView.setAdapter(adapter);

        profileButton.setOnClickListener(v ->
                startActivity(new Intent(InicioEntrenadorActivity.this, PerfilEntrenadorActivity.class)));

        // Cargar lista de alumnos usando el Repositorio
        loadStudentsFromApi();
    }

    // 3. MÉTODO CORREGIDO: Usando UserRepository
    private void loadStudentsFromApi() {
        if (loadingIndicator != null) loadingIndicator.setVisibility(View.VISIBLE);

        // Usamos el método del repositorio que ya maneja el token y la conexión
        userRepository.getAllUsers(this, new UserRepository.GetAllUsersCallback() {
            @Override
            public void onSuccess(List<UserResponse> users) {
                if (loadingIndicator != null) loadingIndicator.setVisibility(View.GONE);
                // Filtramos los datos recibidos
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
        // 1. Limpiamos la lista local de la Activity
        studentList.clear();

        if (allUsers != null) {
            for (UserResponse user : allUsers) {
                // Conversión segura a String
                String userRoleStr = String.valueOf(user.getRole());
                String userGymStr = user.getGymName();

                // Logs para depuración
                android.util.Log.d("FILTRO_DEBUG", "Analizando: " + user.getName() +
                        " | Rol API: " + userRoleStr +
                        " | Gym API: " + userGymStr);

                // Comparación
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

        // 2. CAMBIO CRÍTICO:
        // En vez de solo notifyDataSetChanged, le pasamos los datos al adaptador.
        if (adapter != null) {
            adapter.setStudents(studentList);
        }
    }

    // Método para obtener rutinas y navegar
    private void fetchRoutinesAndNavigate(TrainerStudent student) {
        if (loadingIndicator != null) loadingIndicator.setVisibility(View.VISIBLE);

        Long studentId = Long.parseLong(student.getId());

        routineRepository.getRoutinesByUserId(this, studentId, new RoutineRepository.GetAllRoutinesCallback() {
            @Override
            public void onSuccess(List<Routine> routines) {
                if (loadingIndicator != null) loadingIndicator.setVisibility(View.GONE);

                // 1. Extraer solo los IDs de las rutinas para pasar en el Intent
                ArrayList<String> routineIds = new ArrayList<>();
                if (routines != null) {
                    for (Routine routine : routines) {
                        routineIds.add(routine.getId());
                    }
                }

                // 2. Navegar a la siguiente actividad con los datos reales
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