package com.example.gym_app.data;

import android.content.Context;

import com.example.gym_app.R;
import com.example.gym_app.model.AdminUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdminUserLocalDataSource {

    public List<AdminUser> getUsers(Context context) {
        try {
            String json = readJsonFile(context);
            return parseUsers(json);
        } catch (IOException | JSONException exception) {
            exception.printStackTrace();
            return Collections.emptyList();
        }
    }

    private String readJsonFile(Context context) throws IOException {
        InputStream inputStream = context.getResources().openRawResource(R.raw.admin_users);
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

    private List<AdminUser> parseUsers(String json) throws JSONException {
        List<AdminUser> users = new ArrayList<>();
        JSONArray array = new JSONArray(json);
        for (int index = 0; index < array.length(); index++) {
            JSONObject item = array.getJSONObject(index);
            String name = item.optString("name", "");
            String role = item.optString("role", "");
            users.add(new AdminUser(name, role));
        }
        return users;
    }
}