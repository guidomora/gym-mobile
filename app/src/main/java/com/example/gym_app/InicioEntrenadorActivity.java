package com.example.gym_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gym_app.adapter.TrainerStudentsAdapter;
import com.example.gym_app.data.TrainerDashboardLocalDataSource;
import com.example.gym_app.model.TrainerStudent;
import java.util.ArrayList;

public class InicioEntrenadorActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_entrenador);

        TextView trainerNameTextView = findViewById(R.id.trainerName);
        RecyclerView studentsRecyclerView = findViewById(R.id.recycler_students);
        LinearLayout profileButton = findViewById(R.id.nav_profile);

        studentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        TrainerDashboardLocalDataSource dashboardData = TrainerDashboardLocalDataSource.loadFromResource(this);
        if (dashboardData.getTrainerName() != null && !dashboardData.getTrainerName().isEmpty()) {
            trainerNameTextView.setText(dashboardData.getTrainerName());
        }

        TrainerStudentsAdapter adapter = new TrainerStudentsAdapter(dashboardData.getStudents(), new TrainerStudentsAdapter.OnStudentClickListener() {
            @Override
            public void onStudentSelected(TrainerStudent student) {
                Intent intent = new Intent(InicioEntrenadorActivity.this, RutinasEntrenadorActivity.class);
                intent.putExtra(RutinasEntrenadorActivity.EXTRA_STUDENT_ID, student.getId());
                intent.putExtra(RutinasEntrenadorActivity.EXTRA_STUDENT_NAME, student.getFullName());
                intent.putStringArrayListExtra(RutinasEntrenadorActivity.EXTRA_STUDENT_ROUTINE_IDS, new ArrayList<>(student.getRoutineIds()));
                startActivity(intent);
            }
        });
        studentsRecyclerView.setAdapter(adapter);

        profileButton.setOnClickListener(v ->
                startActivity(new Intent(InicioEntrenadorActivity.this, PerfilEntrenadorActivity.class)));
    }
}