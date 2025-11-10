package com.example.gym_app;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText passwordInput;
    private CheckBox rememberMeCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameInput = findViewById(R.id.et_username);
        passwordInput = findViewById(R.id.et_password);
        rememberMeCheck = findViewById(R.id.cb_remember_me);
        Button loginButton = findViewById(R.id.btn_login);
        TextView registerButton = findViewById(R.id.btn_register_from_login);

        registerInputListeners();

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

    private void registerInputListeners() {
        usernameInput.addTextChangedListener(createErrorCleaner(usernameInput));
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
        final String username = usernameInput.getText() != null
                ? usernameInput.getText().toString().trim() : "";
        final String password = passwordInput.getText() != null
                ? passwordInput.getText().toString() : "";

        boolean hasError = false;

        if (TextUtils.isEmpty(username)) {
            usernameInput.setError(getString(R.string.error_username_required));
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

        LoginCredentials credentials = new LoginCredentials(username, password, rememberMeCheck.isChecked());
        onLoginDataReady(credentials);
    }

    private void onLoginDataReady(LoginCredentials credentials) {
        // En este punto se podrá hacer la llamada al backend con las credenciales.
        String confirmationMessage = getString(R.string.login_form_ready_message, credentials.getUsername());
        Toast.makeText(this, confirmationMessage, Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, RutinasActivity.class));
        finish();
    }
}