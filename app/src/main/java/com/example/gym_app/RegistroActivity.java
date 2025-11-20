package com.example.gym_app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.KeyEvent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gym_app.data.GymRepository;
import com.example.gym_app.data.auth.AuthRepository;
import com.example.gym_app.data.gyms.GymResponse;
import com.example.gym_app.model.LoginResult;
import com.example.gym_app.navigation.AuthNavigator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RegistroActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText phoneInput;
    private EditText emailInput;
    private EditText birthdateInput;
    private EditText passwordInput;
    private EditText confirmPasswordInput;
    private Spinner gymSpinner;
    private Spinner roleSpinner;
    private Button registerButton;
    private String registerButtonDefaultText;

    private AuthRepository authRepository;
    private GymRepository gymRepository;

    public static Intent createIntent(Context context) {
        return new Intent(context, RegistroActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        bindViews();
        registerInputListeners();

        authRepository = new AuthRepository();
        gymRepository = new GymRepository();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegister();
            }
        });

        confirmPasswordInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });

        loadGyms();
    }

    private void bindViews() {
        usernameInput = findViewById(R.id.et_username_reg);
        phoneInput = findViewById(R.id.et_phone);
        emailInput = findViewById(R.id.et_email);
        birthdateInput = findViewById(R.id.et_birthdate);
        passwordInput = findViewById(R.id.et_password);
        confirmPasswordInput = findViewById(R.id.et_confirm_password);
        gymSpinner = findViewById(R.id.spinner_gym);
        roleSpinner = findViewById(R.id.spinner_role);
        registerButton = findViewById(R.id.btn_register);
        registerButtonDefaultText = registerButton.getText().toString();

        ImageView backButton = findViewById(R.id.btn_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        birthdateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        birthdateInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showDatePicker();
                }
            }
        });
    }

    private void loadGyms() {
        // 1. Crear la lista manual con los nombres EXACTOS que acepta el backend
        List<String> gymNames = new ArrayList<>();
        gymNames.add("Fitter");      // Opción 0 (Por defecto)
        gymNames.add("Gold Gym");
        gymNames.add("Super Sport");

        // 2. Configurar el adaptador básico de Android para Strings
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                gymNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // 3. Asignar al Spinner
        if (gymSpinner != null) {
            gymSpinner.setAdapter(adapter);
            gymSpinner.setEnabled(true); // Asegurar que esté habilitado
            gymSpinner.setSelection(0);  // Forzar que seleccione "Fitter" visualmente
        }
    }
    /*private void loadGyms() {
        gymSpinner.setEnabled(false);

        gymRepository.getAllGyms(this, new GymRepository.GetAllGymsCallback() {
            @Override
            public void onSuccess(List<GymResponse> gyms) {
                if (gyms == null || gyms.isEmpty()) {
                    Toast.makeText(RegistroActivity.this, "No hay gimnasios disponibles", Toast.LENGTH_SHORT).show();
                    return;
                }

                ArrayAdapter<GymResponse> adapter = new ArrayAdapter<>(
                        RegistroActivity.this,
                        android.R.layout.simple_spinner_item,
                        gyms
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                gymSpinner.setAdapter(adapter);
                gymSpinner.setEnabled(true);
            }

            @Override
            public void onError(String errorMessage) {
                gymSpinner.setEnabled(true);
            }
        });
    }
*/
    private void registerInputListeners() {
        usernameInput.addTextChangedListener(createErrorCleaner(usernameInput));
        phoneInput.addTextChangedListener(createErrorCleaner(phoneInput));
        emailInput.addTextChangedListener(createErrorCleaner(emailInput));
        birthdateInput.addTextChangedListener(createErrorCleaner(birthdateInput));
        passwordInput.addTextChangedListener(createErrorCleaner(passwordInput));
        confirmPasswordInput.addTextChangedListener(createErrorCleaner(confirmPasswordInput));
    }

    private TextWatcher createErrorCleaner(final EditText target) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                target.setError(null);
            }
        };
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String formatted = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year);
                        birthdateInput.setText(formatted);
                        birthdateInput.setError(null);
                    }
                },
                year,
                month,
                day
        );
        dialog.show();
    }

    private void attemptRegister() {
        String username = usernameInput.getText() != null ? usernameInput.getText().toString().trim() : "";
        String phone = phoneInput.getText() != null ? phoneInput.getText().toString().trim() : "";
        String email = emailInput.getText() != null ? emailInput.getText().toString().trim() : "";
        String birthdate = birthdateInput.getText() != null ? birthdateInput.getText().toString().trim() : "";
        String password = passwordInput.getText() != null ? passwordInput.getText().toString() : "";
        String confirmPassword = confirmPasswordInput.getText() != null ? confirmPasswordInput.getText().toString() : "";

        String selectedGymName = "";
        Object selectedItem = gymSpinner.getSelectedItem();

        if (selectedItem instanceof GymResponse) {
            selectedGymName = ((GymResponse) selectedItem).getNombre();
        } else if (selectedItem != null) {
            selectedGymName = selectedItem.toString();
        }

        String selectedRole = roleSpinner.getSelectedItem() != null ? roleSpinner.getSelectedItem().toString() : "";

        boolean hasError = false;

        if (TextUtils.isEmpty(username)) {
            usernameInput.setError(getString(R.string.error_username_required));
            hasError = true;
        }

        if (!TextUtils.isEmpty(phone) && phone.length() < 7) {
            phoneInput.setError(getString(R.string.error_phone_invalid));
            hasError = true;
        }

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError(getString(R.string.error_email_invalid));
            hasError = true;
        }

        if (TextUtils.isEmpty(birthdate)) {
            birthdateInput.setError(getString(R.string.error_birthdate_required));
            hasError = true;
        }

        String apiBirthdate = null;
        if (!TextUtils.isEmpty(birthdate)) {
            apiBirthdate = formatBirthdateForApi(birthdate);
            if (apiBirthdate == null) {
                birthdateInput.setError(getString(R.string.error_birthdate_format));
                hasError = true;
            }
        }

        if (TextUtils.isEmpty(password)) {
            passwordInput.setError(getString(R.string.error_password_required));
            hasError = true;
        } else if (password.length() < 6) {
            passwordInput.setError(getString(R.string.error_password_length));
            hasError = true;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordInput.setError(getString(R.string.error_password_mismatch));
            hasError = true;
        }

        if (TextUtils.isEmpty(selectedGymName)) {
            Toast.makeText(this, R.string.error_gym_required, Toast.LENGTH_SHORT).show();
            hasError = true;
        }

        String apiRole = mapRoleToApiValue(selectedRole);
        if (TextUtils.isEmpty(apiRole)) {
            Toast.makeText(this, R.string.error_role_required, Toast.LENGTH_SHORT).show();
            hasError = true;
        }

        if (hasError) {
            return;
        }

        String sanitizedPhone = TextUtils.isEmpty(phone) ? null : phone;

        RegisterFormData formData = new RegisterFormData(
                username,
                sanitizedPhone,
                email,
                apiBirthdate,
                apiRole,
                password,
                selectedGymName
        );

        onRegisterDataReady(formData);
    }

    private void onRegisterDataReady(RegisterFormData data) {
        setLoading(true);
        authRepository.register(this, data, new AuthRepository.RegisterCallback() {
            @Override
            public void onSuccess(LoginResult result) {
                setLoading(false);
                String message = result.getMessage();
                if (TextUtils.isEmpty(message)) {
                    message = getString(R.string.register_success_message, result.getDisplayName());
                }
                Toast.makeText(RegistroActivity.this, message, Toast.LENGTH_LONG).show();
                String resolvedRole = !TextUtils.isEmpty(result.getRole())
                        ? result.getRole()
                        : data.getRole();
                AuthNavigator.navigateToHome(RegistroActivity.this, resolvedRole, true);
            }

            @Override
            public void onError(String errorMessage) {
                setLoading(false);
                Toast.makeText(RegistroActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoading(boolean isLoading) {
        usernameInput.setEnabled(!isLoading);
        phoneInput.setEnabled(!isLoading);
        emailInput.setEnabled(!isLoading);
        birthdateInput.setEnabled(!isLoading);
        passwordInput.setEnabled(!isLoading);
        confirmPasswordInput.setEnabled(!isLoading);
        gymSpinner.setEnabled(!isLoading);
        roleSpinner.setEnabled(!isLoading);
        registerButton.setEnabled(!isLoading);
        if (isLoading) {
            registerButton.setText(getString(R.string.register_loading_label));
        } else {
            registerButton.setText(registerButtonDefaultText);
        }
    }

    private String formatBirthdateForApi(String birthdate) {
        SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        displayFormat.setLenient(false);
        SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {
            Date parsedDate = displayFormat.parse(birthdate);
            if (parsedDate != null) {
                return apiFormat.format(parsedDate);
            }
        } catch (ParseException ignored) {
            // Ignored
        }
        return null;
    }

    private String mapRoleToApiValue(String selectedRole) {
        if (TextUtils.isEmpty(selectedRole)) {
            return null;
        }
        String studentLabel = getString(R.string.register_role_student);
        String trainerLabel = getString(R.string.register_role_trainer);
        if (selectedRole.equalsIgnoreCase(studentLabel)) {
            return "STUDENT";
        }
        if (selectedRole.equalsIgnoreCase(trainerLabel)) {
            return "TRAINER";
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (authRepository != null) {
            authRepository.cancelOngoingRegister();
        }
    }
}