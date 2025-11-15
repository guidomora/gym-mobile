package com.example.gym_app;

import android.content.Intent;
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

import com.example.gym_app.data.auth.AuthRepository;
import com.example.gym_app.data.auth.SavedLoginData;
import com.example.gym_app.model.LoginCredentials;
import com.example.gym_app.model.LoginResult;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput;
    private EditText passwordInput;
    private CheckBox rememberMeCheck;
    private Button loginButton;
    private String loginButtonDefaultText;
    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authRepository = new AuthRepository();

        emailInput = findViewById(R.id.et_username);
        passwordInput = findViewById(R.id.et_password);
        rememberMeCheck = findViewById(R.id.cb_remember_me);
        loginButton = findViewById(R.id.btn_login);
        loginButtonDefaultText = loginButton.getText().toString();
        TextView registerButton = findViewById(R.id.btn_register_from_login);

        registerInputListeners();
        prefillSavedCredentials();

        passwordInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(RegistroActivity.createIntent(LoginActivity.this));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (authRepository != null) {
            authRepository.cancelOngoingLogin();
        }
    }

    private void prefillSavedCredentials() {
        SavedLoginData savedLoginData = authRepository.getSavedLoginData(this);
        rememberMeCheck.setChecked(savedLoginData.isRememberMe());
        if (savedLoginData.shouldPrefillEmail()) {
            emailInput.setText(savedLoginData.getEmail());
        }
    }

    private void registerInputListeners() {
        emailInput.addTextChangedListener(createErrorCleaner(emailInput));
        passwordInput.addTextChangedListener(createErrorCleaner(passwordInput));
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

    private void attemptLogin() {
        final String email = emailInput.getText() != null
                ? emailInput.getText().toString().trim() : "";
        final String password = passwordInput.getText() != null
                ? passwordInput.getText().toString() : "";

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

        LoginCredentials credentials = new LoginCredentials(email, password, rememberMeCheck.isChecked());
        onLoginDataReady(credentials);
    }

    private void onLoginDataReady(final LoginCredentials credentials) {
        setLoading(true);
        authRepository.login(this, credentials, new AuthRepository.LoginCallback() {
            @Override
            public void onSuccess(LoginResult result) {
                setLoading(false);
                String message = result.getMessage();
                if (TextUtils.isEmpty(message)) {
                    message = getString(R.string.login_success_message, result.getDisplayName());
                }
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, InicioEntrenadorActivity.class));
                finish();
            }

            @Override
            public void onError(String errorMessage) {
                setLoading(false);
                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoading(boolean isLoading) {
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
}