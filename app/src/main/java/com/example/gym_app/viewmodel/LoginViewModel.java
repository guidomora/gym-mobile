package com.example.gym_app.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.gym_app.data.auth.AuthRepository;
import com.example.gym_app.data.auth.SavedLoginData;
import com.example.gym_app.model.LoginCredentials;
import com.example.gym_app.model.LoginResult;

public class LoginViewModel extends AndroidViewModel {

    private final AuthRepository authRepository;

    private final MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public LoginViewModel(@NonNull Application application) {
        super(application);
        this.authRepository = new AuthRepository();
    }

    public LiveData<LoginResult> getLoginResult() { return loginResult; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    public void login(String email, String password, boolean rememberMe) {
        isLoading.setValue(true);

        LoginCredentials credentials = new LoginCredentials(email, password, rememberMe);

        authRepository.login(getApplication(), credentials, new AuthRepository.LoginCallback() {
            @Override
            public void onSuccess(LoginResult result) {
                isLoading.postValue(false);
                loginResult.postValue(result);
            }

            @Override
            public void onError(String error) {
                isLoading.postValue(false);
                errorMessage.postValue(error);
            }
        });
    }

    public SavedLoginData getSavedCredentials(android.content.Context context) {
        return authRepository.getSavedLoginData(context);
    }
}