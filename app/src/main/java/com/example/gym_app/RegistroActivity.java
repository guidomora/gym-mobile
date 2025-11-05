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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class RegistroActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText phoneInput;
    private EditText emailInput;
    private EditText birthdateInput;
    private EditText passwordInput;
    private EditText confirmPasswordInput;
    private Spinner gymSpinner;

    public static Intent createIntent(Context context) {
        return new Intent(context, RegistroActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        bindViews();
        registerInputListeners();

        Button registerButton = findViewById(R.id.btn_register);
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
    }

    private void bindViews() {
        usernameInput = findViewById(R.id.et_username_reg);
        phoneInput = findViewById(R.id.et_phone);
        emailInput = findViewById(R.id.et_email);
        birthdateInput = findViewById(R.id.et_birthdate);
        passwordInput = findViewById(R.id.et_password);
        confirmPasswordInput = findViewById(R.id.et_confirm_password);
        gymSpinner = findViewById(R.id.spinner_gym);

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

    private void registerInputListeners() {
        usernameInput.addTextChangedListener(createErrorCleaner(usernameInput));
        phoneInput.addTextChangedListener(createErrorCleaner(phoneInput));
        emailInput.addTextChangedListener(createErrorCleaner(emailInput));
        passwordInput.addTextChangedListener(createErrorCleaner(passwordInput));
        confirmPasswordInput.addTextChangedListener(createErrorCleaner(confirmPasswordInput));
    }

    private TextWatcher createErrorCleaner(final EditText target) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No-op
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No-op
            }

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
                        String formatted = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
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
        String selectedGym = gymSpinner.getSelectedItem() != null ? gymSpinner.getSelectedItem().toString() : "";

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

        if (TextUtils.isEmpty(selectedGym)) {
            Toast.makeText(this, R.string.error_gym_required, Toast.LENGTH_SHORT).show();
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
                birthdate,
                password,
                selectedGym
        );

        onRegisterDataReady(formData);
    }

    private void onRegisterDataReady(RegisterFormData data) {
        // Aquí se podrá enviar la información al backend.
        String message = getString(R.string.register_form_ready_message, data.getUsername());
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}