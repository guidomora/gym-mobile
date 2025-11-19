package com.example.gym_app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.gym_app.viewmodel.EditProfileViewModel;

public class EditarPerfilActivity extends AppCompatActivity {

    private EditProfileViewModel viewModel;
    private EditText etName, etPhone, etDate;
    private Button btnGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        viewModel = new ViewModelProvider(this).get(EditProfileViewModel.class);

        etName = findViewById(R.id.et_edit_name);
        etPhone = findViewById(R.id.et_edit_phone);
        etDate = findViewById(R.id.et_edit_birthdate);
        btnGuardar = findViewById(R.id.btn_confirm_edit);

        // Observar resultados
        viewModel.getSaveSuccess().observe(this, message -> {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            finish(); // Cierra la pantalla y vuelve al perfil
        });

        viewModel.getSaveError().observe(this, error -> {
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            btnGuardar.setEnabled(true);
            btnGuardar.setText("Guardar Cambios");
        });

        viewModel.getIsSaving().observe(this, isSaving -> {
            btnGuardar.setEnabled(!isSaving);
            if (isSaving) btnGuardar.setText("Guardando...");
        });

        // Acción del botón
        btnGuardar.setOnClickListener(v -> {
            String name = etName.getText().toString();
            String phone = etPhone.getText().toString();
            String date = etDate.getText().toString();

            // Validaciones básicas
            if (name.isEmpty() || phone.isEmpty() || date.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.updateProfile(name, phone, date);
        });
    }
}