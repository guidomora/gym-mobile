package com.example.gym_app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.gym_app.databinding.ActivityPerfilBinding; // Asegúrate de que esto se genere
import com.example.gym_app.viewmodel.ProfileViewModel;

public class PerfilActivity extends AppCompatActivity {

    private ActivityPerfilBinding binding;
    private ProfileViewModel viewModel;

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bundle extras = result.getData().getExtras();
                    if (extras != null) {
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        if (binding != null) {
                            binding.ivProfilePic.setImageBitmap(imageBitmap);
                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        binding = ActivityPerfilBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        viewModel.getLogoutSuccess().observe(this, success -> {
            if (Boolean.TRUE.equals(success)) {
                Intent intent = new Intent(PerfilActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });


        binding.navHome.setOnClickListener(v -> {
            Intent intent = new Intent(PerfilActivity.this, RutinasActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        binding.navToday.setOnClickListener(v ->
                startActivity(new Intent(PerfilActivity.this, HoyActivity.class))
        );

        binding.ivProfilePic.setOnClickListener(v -> openCamera());

        binding.btnSave.setOnClickListener(v -> viewModel.logout());

        binding.btnEditProfile.setOnClickListener(v ->
                startActivity(new Intent(PerfilActivity.this, EditarPerfilActivity.class))
        );

        if (binding.btnLinkMembership != null) {
            binding.btnLinkMembership.setOnClickListener(v -> {
                Toast.makeText(this, "Función de membresía en construcción", Toast.LENGTH_SHORT).show();
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