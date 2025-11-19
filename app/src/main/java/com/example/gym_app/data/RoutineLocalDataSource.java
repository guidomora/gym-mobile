package com.example.gym_app.data;

import android.content.Context;

import com.example.gym_app.R;
import com.example.gym_app.model.Exercise;
import com.example.gym_app.model.Routine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RoutineLocalDataSource {

    public List<Routine> getRoutines(Context context) {
        try {
            JSONArray routinesJson = readRoutinesArray(context);
            return parseRoutines(routinesJson);
        } catch (IOException | JSONException exception) {
            exception.printStackTrace();
            return Collections.emptyList();
        }
    }

    public Routine getRoutineById(Context context, String routineId) {
        if (routineId == null || routineId.isEmpty()) {
            return null;
        }
        try {
            JSONArray routinesJson = readRoutinesArray(context);
            List<Routine> routines = parseRoutines(routinesJson);
            for (Routine routine : routines) {
                if (routineId.equals(routine.getId())) {
                    return routine;
                }
            }
        } catch (IOException | JSONException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    private JSONArray readRoutinesArray(Context context) throws IOException, JSONException {
        String json = readJsonFile(context);
        Object parsed = new JSONTokener(json).nextValue();
        if (parsed instanceof JSONArray) {
            return (JSONArray) parsed;
        }
        if (parsed instanceof JSONObject) {
            JSONObject root = (JSONObject) parsed;
            JSONArray array = root.optJSONArray("routines");
            if (array != null) {
                return array;
            }
        }
        return new JSONArray();
    }

    private String readJsonFile(Context context) throws IOException {
        InputStream inputStream = context.getResources().openRawResource(R.raw.routines);
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
            return outputStream.toString(StandardCharsets.UTF_8.name());
        } finally {
            inputStream.close();
        }
    }

    private List<Routine> parseRoutines(JSONArray array) throws JSONException {
        List<Routine> routines = new ArrayList<>();
        for (int index = 0; index < array.length(); index++) {
            JSONObject item = array.optJSONObject(index);
            if (item == null) {
                continue;
            }
            String id = item.optString("id", "");
            String name = item.optString("name", "");
            int duration = item.optInt("duration", 0);
            String day = item.optString("day", "");

            if (id.isEmpty() && !name.isEmpty()) {
                id = name + "_" + day;
            }

            List<Exercise> exercises = parseExercises(item.optJSONArray("exercises"));

            if (!name.isEmpty()) {
                routines.add(new Routine(id, name, duration, day, exercises));
            }
        }
        return routines;
    }

    private List<Exercise> parseExercises(JSONArray exercisesArray) {
        if (exercisesArray == null) {
            return Collections.emptyList();
        }
        List<Exercise> exercises = new ArrayList<>();
        for (int index = 0; index < exercisesArray.length(); index++) {
            JSONObject exerciseJson = exercisesArray.optJSONObject(index);
            if (exerciseJson == null) {
                continue;
            }
            String name = exerciseJson.optString("name", "");
            String setsReps = exerciseJson.optString("setsReps", "");
            String rest = exerciseJson.optString("rest", "");
            if (!name.isEmpty()) {
                exercises.add(new Exercise(name, setsReps, rest));
            }
        }
        return exercises;
    }
}