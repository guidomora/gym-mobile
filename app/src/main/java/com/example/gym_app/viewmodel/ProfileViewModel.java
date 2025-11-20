package com.example.gym_app.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.gym_app.data.auth.AuthRepository;
import com.example.gym_app.data.auth.SavedLoginData;
import com.example.gym_app.model.PerfilUser;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileViewModel extends AndroidViewModel {

    private final AuthRepository authRepository;
    private final MutableLiveData<Boolean> logoutSuccess = new MutableLiveData<>();
    private final MutableLiveData<ProfileUiState> profileUiState = new MutableLiveData<>();
    private final ExecutorService executor;

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        this.authRepository = new AuthRepository();
        this.executor = Executors.newSingleThreadExecutor();
    }

    public LiveData<ProfileUiState> getUiState() { return profileUiState; }

    public LiveData<Boolean> getLogoutSuccess() { return logoutSuccess; }

    public void fetchPerfilUserData() {
        profileUiState.setValue(new ProfileUiState.Loading());
        executor.execute(() -> {
            try {
                SavedLoginData data = authRepository.getSavedLoginData(getApplication());

                if (data != null && data.getAuthToken() != null && !data.getAuthToken().isEmpty()) {
                    profileUiState.postValue(new ProfileUiState.Success(new PerfilUser(

                    )));
                }
            } catch (Exception e) {
                profileUiState.postValue(new ProfileUiState.Error("Fallo de ejecucion asincrona: " + e.getMessage()));
            }
        });
    }
    public void linkMembership(String qrContent) {
    }

    public void logout() {
        authRepository.clearSession(getApplication());
        logoutSuccess.setValue(true);
    }
}