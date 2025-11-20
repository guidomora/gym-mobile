package com.example.gym_app;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.gym_app.data.auth.SavedLoginData;
import com.example.gym_app.navigation.AuthNavigator;
import com.example.gym_app.viewmodel.LoginViewModel;
import android.content.Intent;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel viewModel;
    private EditText emailInput;
    private EditText passwordInput;
    private CheckBox rememberMeCheck;
    private Button loginButton;
    private String loginButtonDefaultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        emailInput = findViewById(R.id.et_username);
        passwordInput = findViewById(R.id.et_password);
        rememberMeCheck = findViewById(R.id.cb_remember_me);
        loginButton = findViewById(R.id.btn_login);
        loginButtonDefaultText = loginButton.getText().toString();
        TextView registerButton = findViewById(R.id.btn_register_from_login);

        setupInputListeners();

        prefillSavedCredentials();

        observeViewModel();

        passwordInput.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                attemptLogin();
                return true;
            }
            return false;
        });

        loginButton.setOnClickListener(v -> attemptLogin());


        registerButton.setOnClickListener(v ->
                startActivity(RegistroActivity.createIntent(LoginActivity.this))
        );
    }

    private void prefillSavedCredentials() {
        SavedLoginData savedLoginData = viewModel.getSavedCredentials(this);
        rememberMeCheck.setChecked(savedLoginData.isRememberMe());
        if (savedLoginData.shouldPrefillEmail()) {
            emailInput.setText(savedLoginData.getEmail());
        }
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            setLoadingState(isLoading);
        });

        viewModel.getLoginResult().observe(this, result -> {
            String message = result.getMessage();
            if (TextUtils.isEmpty(message)) {
                message = getString(R.string.login_success_message, result.getDisplayName());
            }
            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();

            AuthNavigator.navigateToHome(LoginActivity.this, result.getRole(), false);
        });

        viewModel.getErrorMessage().observe(this, errorMsg -> {
            Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_LONG).show();
        });
    }

    private void attemptLogin() {
        final String email = emailInput.getText() != null ? emailInput.getText().toString().trim() : "";
        final String password = passwordInput.getText() != null ? passwordInput.getText().toString() : "";

        boolean hasError = false;

        if (TextUtils.isEmpty(email)) {
            emailInput.setError(getString(R.string.error_email_required));
            hasError = true;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError(getString(R.string.error_email_invalid));
            hasError = true;
        }

        if (TextUtils.isEmpty(password)) {
            passwordInput.setError(getString(R.string.error_password_required));
            hasError = true;
        } else if (password.length() < 6) {
            passwordInput.setError(getString(R.string.error_password_length));
            hasError = true;
        }

        if (hasError) {
            return;
        }

        viewModel.login(email, password, rememberMeCheck.isChecked());
    }

    private void setLoadingState(boolean isLoading) {
        emailInput.setEnabled(!isLoading);
        passwordInput.setEnabled(!isLoading);
        rememberMeCheck.setEnabled(!isLoading);
        loginButton.setEnabled(!isLoading);

        if (isLoading) {
            loginButton.setText(getString(R.string.login_loading_label));
        } else {
            loginButton.setText(loginButtonDefaultText);
        }
    }

    private void setupInputListeners() {
        TextWatcher errorCleaner = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (emailInput.hasFocus()) emailInput.setError(null);
                if (passwordInput.hasFocus()) passwordInput.setError(null);
            }
        };
        emailInput.addTextChangedListener(errorCleaner);
        passwordInput.addTextChangedListener(errorCleaner);
    }
}