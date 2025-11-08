package com.example.gym_app;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gym_app.adapter.AdminUserAdapter;
import com.example.gym_app.data.AdminUserLocalDataSource;
import com.example.gym_app.model.AdminUser;

import java.util.List;

public class InicioAdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_admin);

        RecyclerView recyclerView = findViewById(R.id.adminUsersRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        AdminUserLocalDataSource dataSource = new AdminUserLocalDataSource();
        List<AdminUser> users = dataSource.getUsers(this);

        AdminUserAdapter adapter = new AdminUserAdapter(this, users);
        recyclerView.setAdapter(adapter);
    }
}