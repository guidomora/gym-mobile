package com.example.gym_app;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gym_app.adapter.ExerciseAdapter;
import com.example.gym_app.data.RoutineRepository;
import com.example.gym_app.data.auth.AuthSessionManager;
import com.example.gym_app.data.exercises.ExerciseRepository;
import com.example.gym_app.model.Exercise;
import com.example.gym_app.model.Routine;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HoyActivity extends AppCompatActivity {

    private RoutineRepository routineRepository;
    private ExerciseRepository exerciseRepository;
    private AuthSessionManager authSessionManager;
    private ExerciseAdapter exerciseAdapter;
    
    private TextView routineTitle;
    private TextView emptyState;
    private RecyclerView exercisesRecyclerView;
    private ProgressBar progressBar;
    private ImageButton backButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hoy);

        routineRepository = new RoutineRepository();
        exerciseRepository = new ExerciseRepository();
        authSessionManager = new AuthSessionManager();

        LinearLayout homeButton = findViewById(R.id.nav_home);
        LinearLayout profileButton = findViewById(R.id.nav_profile);
        routineTitle = findViewById(R.id.tv_routine_title);
        exercisesRecyclerView = findViewById(R.id.rv_exercises); // Necesita ser agregado en XML si no existe, o reutilizar scroll
        backButton = findViewById(R.id.btn_back);
        progressBar = findViewById(R.id.progress_bar); // Necesita ser agregado en XML
        emptyState = findViewById(R.id.tv_empty_state); // Necesita ser agregado en XML

        // Setup Recycler View
        // IMPORTANTE: El layout actual activity_hoy.xml usa un ScrollView estático.
        // Para que esto funcione correctamente con datos dinámicos, deberíamos reemplazar el ScrollView 
        // con un RecyclerView en el XML, o programáticamente ocultar el ScrollView y mostrar un RecyclerView.
        // Dado que voy a modificar el Java, asumiré que el usuario también quiere que modifique el XML para soportar la lista dinámica,
        // O intentaré inyectar los datos en el RecyclerView si logro modificar el XML.
        // Por ahora, voy a configurar el RecyclerView asumiendo que modificaré el XML en el siguiente paso.
        
        exercisesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        exerciseAdapter = new ExerciseAdapter();
        exercisesRecyclerView.setAdapter(exerciseAdapter);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HoyActivity.this, RutinasActivity.class));
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HoyActivity.this, PerfilActivity.class));
            }
        });
        
        backButton.setOnClickListener(v -> finish());
        
        loadTodayRoutine();
    }
    
    private void loadTodayRoutine() {
        showLoading(true);
        Long userId = authSessionManager.getUserId(this);
        
        if (userId == null) {
            showLoading(false);
            showEmptyState(true, "Usuario no identificado");
            return;
        }

        // 1. Obtener todas las rutinas del usuario
        routineRepository.getRoutinesByUserId(this, userId, new RoutineRepository.GetAllRoutinesCallback() {
            @Override
            public void onSuccess(List<Routine> routines) {
                if (routines == null || routines.isEmpty()) {
                    showLoading(false);
                    showEmptyState(true, "No tienes rutinas asignadas");
                    return;
                }
                
                // 2. Filtrar la rutina de hoy
                Routine todayRoutine = findTodayRoutine(routines);
                
                if (todayRoutine == null) {
                    showLoading(false);
                    showEmptyState(true, "No hay rutina para el día de hoy");
                    return;
                }
                
                routineTitle.setText(todayRoutine.getName() + " - Hoy");
                
                // 3. Cargar ejercicios de la rutina de hoy
                loadExercises(todayRoutine);
            }

            @Override
            public void onError(@NonNull String errorMessage) {
                showLoading(false);
                showEmptyState(true, errorMessage);
            }
        });
    }

    private Routine findTodayRoutine(List<Routine> routines) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return null;
        }

        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = today.getDayOfWeek();

        // Formato en español
        String targetDaySpanish = mapDayToSpanish(dayOfWeek);
        // Formato en inglés (como viene de la API)
        String targetDayEnglish = dayOfWeek.name(); // "THURSDAY", "MONDAY", etc.

        for (Routine routine : routines) {
            String routineDay = routine.getDayOfWeek();
            if (routineDay != null) {
                if (routineDay.equalsIgnoreCase(targetDaySpanish) ||
                        routineDay.equalsIgnoreCase(targetDayEnglish)) {
                    return routine;
                }
            }
        }
        return null;
    }

    private String mapDayToSpanish(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY: return "Lunes";
            case TUESDAY: return "Martes";
            case WEDNESDAY: return "Miércoles";
            case THURSDAY: return "Jueves";
            case FRIDAY: return "Viernes";
            case SATURDAY: return "Sábado";
            case SUNDAY: return "Domingo";
            default: return "";
        }
    }

    private void loadExercises(Routine routine) {
        // Si el objeto routine ya tiene ejercicios cargados (depende de la impl del repo), usarlos.
        // Pero RoutineRepository.getRoutinesByUserId mapea RoutineResponse que no trae ejercicios anidados usualmente en listas
        // (mirando RoutineRepository.mapRoutineResponse, pone new ArrayList<Exercise>()).
        // Así que hay que buscar los ejercicios por ID de rutina.
        
        try {
            Long routineId = Long.parseLong(routine.getId());
            exerciseRepository.getExercisesByRoutine(this, routineId, new ExerciseRepository.GetExercisesCallback() {
                @Override
                public void onSuccess(List<Exercise> exercises) {
                    showLoading(false);
                    if (exercises == null || exercises.isEmpty()) {
                         showEmptyState(true, "La rutina de hoy no tiene ejercicios");
                    } else {
                        showEmptyState(false, null);
                        exerciseAdapter.submitList(exercises);
                    }
                }

                @Override
                public void onError(@NonNull String errorMessage) {
                    showLoading(false);
                    showEmptyState(true, "Error cargando ejercicios: " + errorMessage);
                }
            });
        } catch (NumberFormatException e) {
             showLoading(false);
             showEmptyState(true, "Error en ID de rutina");
        }
    }

    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (exercisesRecyclerView != null) {
            exercisesRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
        if (emptyState != null && show) {
            emptyState.setVisibility(View.GONE);
        }
        // Ocultar scroll estático si existe y estamos cargando/mostrando recycler
        View staticContent = findViewById(R.id.scroll_content);
        if (staticContent != null) {
            staticContent.setVisibility(View.GONE);
        }
    }

    private void showEmptyState(boolean show, String message) {
        if (emptyState != null) {
            emptyState.setVisibility(show ? View.VISIBLE : View.GONE);
            if (message != null) {
                emptyState.setText(message);
            }
        }
        if (exercisesRecyclerView != null) {
            exercisesRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
        View staticContent = findViewById(R.id.scroll_content);
        if (staticContent != null) {
             staticContent.setVisibility(View.GONE);
        }
    }
}