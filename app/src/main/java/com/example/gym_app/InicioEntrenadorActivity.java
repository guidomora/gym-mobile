package com.example.gym_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gym_app.adapter.TrainerStudentsAdapter;
import com.example.gym_app.data.TrainerDashboardLocalDataSource;
import com.example.gym_app.model.TrainerStudent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class InicioEntrenadorActivity extends AppCompatActivity {

    private static final String TAG = "InicioEntrenadorActivity";
    private static final String DASHBOARD_ASSET = "trainer_dashboard.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_entrenador);

        TextView trainerNameTextView = findViewById(R.id.trainerName);
        RecyclerView studentsRecyclerView = findViewById(R.id.recycler_students);
        LinearLayout profileButton = findViewById(R.id.nav_profile);

        studentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        TrainerDashboardLocalDataSource dashboardData = loadDashboardDataFromAssets();
        if (!TextUtils.isEmpty(dashboardData.getTrainerName())) {
            trainerNameTextView.setText(dashboardData.getTrainerName());
        }

        TrainerStudentsAdapter adapter = new TrainerStudentsAdapter(dashboardData.getStudents(), new TrainerStudentsAdapter.OnStudentClickListener() {            @Override
            public void onStudentSelected(TrainerStudent student) {
                startActivity(new Intent(InicioEntrenadorActivity.this, RutinasEntrenadorActivity.class));
            }
        });
        studentsRecyclerView.setAdapter(adapter);

        profileButton.setOnClickListener(v ->
                startActivity(new Intent(InicioEntrenadorActivity.this, PerfilEntrenadorActivity.class)));
    }

    @NonNull
    private TrainerDashboardLocalDataSource  loadDashboardDataFromAssets() {
        String trainerName = null;
        List<TrainerStudent> students = new ArrayList<>();

        try (InputStream inputStream = getAssets().open(DASHBOARD_ASSET);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            JSONObject dashboardObject = new JSONObject(builder.toString());
            JSONObject trainerObject = dashboardObject.optJSONObject("trainer");
            if (trainerObject != null) {
                trainerName = trainerObject.optString("fullName", trainerName);
            }

            JSONArray studentsArray = dashboardObject.optJSONArray("students");
            if (studentsArray != null) {
                for (int i = 0; i < studentsArray.length(); i++) {
                    JSONObject studentObject = studentsArray.optJSONObject(i);
                    if (studentObject == null) {
                        continue;
                    }

                    String id = studentObject.optString("id", null);
                    String fullName = studentObject.optString("fullName", null);
                    if (!TextUtils.isEmpty(fullName)) {
                        students.add(new TrainerStudent(id, fullName));
                    }
                }
            }
        } catch (IOException | JSONException exception) {
            Log.e(TAG, "Error al cargar los datos del entrenador", exception);
        }

        return new TrainerDashboardLocalDataSource(trainerName, students);
    }
}