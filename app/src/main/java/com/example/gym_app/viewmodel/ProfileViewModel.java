package com.example.gym_app.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.gym_app.data.auth.AuthRepository;

public class ProfileViewModel extends AndroidViewModel {

    private final AuthRepository authRepository;
    private final MutableLiveData<Boolean> logoutSuccess = new MutableLiveData<>();

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        this.authRepository = new AuthRepository();
    }

    public LiveData<Boolean> getLogoutSuccess() { return logoutSuccess; }

    public void logout() {
        authRepository.clearSession(getApplication());
        logoutSuccess.setValue(true);
    }
}