package com.example.gym_app.data;

import android.content.Context;

import com.example.gym_app.R;
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
            String json = readJsonFile(context);
            return parseRoutines(json);
        } catch (IOException | JSONException exception) {
            exception.printStackTrace();
            return Collections.emptyList();
        }
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

    private List<Routine> parseRoutines(String json) throws JSONException {
        List<Routine> routines = new ArrayList<>();
        Object parsed = new JSONTokener(json).nextValue();
        JSONArray array;
        if (parsed instanceof JSONArray) {
            array = (JSONArray) parsed;
        } else if (parsed instanceof JSONObject) {
            JSONObject root = (JSONObject) parsed;
            array = root.optJSONArray("routines");
            if (array == null) {
                return routines;
            }
        } else {
            return routines;
        }

        for (int index = 0; index < array.length(); index++) {
            JSONObject item = array.optJSONObject(index);
            if (item == null) {
                continue;
            }
            String name = item.optString("name", "");
            int duration = item.optInt("duration", 0);
            String day = item.optString("day", "");
            if (!name.isEmpty()) {
                routines.add(new Routine(name, duration, day));
            }
        }
        return routines;
    }
}