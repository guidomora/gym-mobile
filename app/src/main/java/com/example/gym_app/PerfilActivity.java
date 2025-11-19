package com.example.gym_app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log; // Import para logs robustos
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.gym_app.viewmodel.ProfileViewModel;

public class PerfilActivity extends AppCompatActivity {

    private ImageView profileImageView;

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bundle extras = result.getData().getExtras();
                    if (extras != null) {
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        if (profileImageView != null) {
                            profileImageView.setImageBitmap(imageBitmap);
                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        ProfileViewModel viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        LinearLayout homeButton = findViewById(R.id.nav_home);
        LinearLayout todayButton = findViewById(R.id.nav_today);
        profileImageView = findViewById(R.id.iv_profile_pic);
        Button btnLogout = findViewById(R.id.btn_save);
        Button btnEditProfile = findViewById(R.id.btn_edit_profile);

        viewModel.getLogoutSuccess().observe(this, success -> {
            if (Boolean.TRUE.equals(success)) {
                Intent intent = new Intent(PerfilActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(PerfilActivity.this, RutinasActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        todayButton.setOnClickListener(v -> {
            startActivity(new Intent(PerfilActivity.this, HoyActivity.class));
        });

        if (profileImageView != null) {
            profileImageView.setOnClickListener(v -> openCamera());
        }

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> viewModel.logout());
        }

        if (btnEditProfile != null) {
            btnEditProfile.setOnClickListener(v -> {
                startActivity(new Intent(PerfilActivity.this, EditarPerfilActivity.class));
            });
        }
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            cameraLauncher.launch(takePictureIntent);
        } catch (Exception e) {
            Log.e("PerfilActivity", "Error al abrir la cámara", e);
            Toast.makeText(this, "No se pudo abrir la cámara", Toast.LENGTH_SHORT).show();
        }
    }
}