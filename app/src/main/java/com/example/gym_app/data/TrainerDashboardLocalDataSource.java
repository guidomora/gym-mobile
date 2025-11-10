package com.example.gym_app.data;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.example.gym_app.R;
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
import java.util.Collections;
import java.util.List;

public class TrainerDashboardLocalDataSource {
    private static final String TAG = "TrainerDashboardData";
    private static final int DEFAULT_RESOURCE = R.raw.trainer_dashboard;

    private final String trainerName;
    private final List<TrainerStudent> students;

    public TrainerDashboardLocalDataSource(String trainerName, List<TrainerStudent> students) {
        this.trainerName = trainerName;
        if (students == null) {
            this.students = Collections.emptyList();
        } else {
            this.students = new ArrayList<>(students);
        }
    }

    public String getTrainerName() {
        return trainerName;
    }

    public List<TrainerStudent> getStudents() {
        return new ArrayList<>(students);
    }

    public TrainerStudent findStudentById(String studentId) {
        if (studentId == null || studentId.isEmpty()) {
            return null;
        }
        for (TrainerStudent student : students) {
            if (studentId.equals(student.getId())) {
                return student;
            }
        }
        return null;
    }

    public static TrainerDashboardLocalDataSource loadFromResource(Context context) {
        return loadFromResource(context, DEFAULT_RESOURCE);
    }

    public static TrainerDashboardLocalDataSource loadFromResource(Context context, int resourceId) {
        if (context == null) {
            return new TrainerDashboardLocalDataSource(null, Collections.<TrainerStudent>emptyList());
        }

        String trainerName = null;
        List<TrainerStudent> students = new ArrayList<>();

        Resources resources = context.getResources();
        try (InputStream inputStream = resources.openRawResource(resourceId);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            JSONObject dashboardObject = new JSONObject(builder.toString());
            JSONObject trainerObject = dashboardObject.optJSONObject("trainer");
            if (trainerObject != null) {
                String name = trainerObject.optString("fullName", null);
                if (name != null && !name.isEmpty()) {
                    trainerName = name;
                }
            }

            JSONArray studentsArray = dashboardObject.optJSONArray("students");
            if (studentsArray != null) {
                for (int index = 0; index < studentsArray.length(); index++) {
                    JSONObject studentObject = studentsArray.optJSONObject(index);
                    if (studentObject == null) {
                        continue;
                    }

                    String id = studentObject.optString("id", null);
                    String fullName = studentObject.optString("fullName", null);

                    if (fullName == null || fullName.isEmpty()) {
                        continue;
                    }

                    List<String> routineIds = new ArrayList<>();
                    JSONArray routineIdsArray = studentObject.optJSONArray("routineIds");
                    if (routineIdsArray != null) {
                        for (int routineIndex = 0; routineIndex < routineIdsArray.length(); routineIndex++) {
                            String routineId = routineIdsArray.optString(routineIndex, null);
                            if (routineId != null && !routineId.isEmpty()) {
                                routineIds.add(routineId);
                            }
                        }
                    }

                    students.add(new TrainerStudent(id, fullName, routineIds));
                }
            }
        } catch (Resources.NotFoundException | IOException | JSONException exception) {
            Log.e(TAG, "Error al cargar los datos del entrenador", exception);
        }

        return new TrainerDashboardLocalDataSource(trainerName, students);
    }
}