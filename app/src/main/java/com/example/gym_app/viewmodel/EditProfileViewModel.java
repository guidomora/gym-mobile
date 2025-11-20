package com.example.gym_app.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.gym_app.data.UserRepository;
import com.example.gym_app.data.user.UpdateProfileRequest;

public class EditProfileViewModel extends AndroidViewModel {

    private final UserRepository userRepository;
    private final MutableLiveData<Boolean> isSaving = new MutableLiveData<>();
    private final MutableLiveData<String> saveSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> saveError = new MutableLiveData<>();

    public EditProfileViewModel(@NonNull Application application) {
        super(application);
        this.userRepository = new UserRepository();
    }

    public LiveData<Boolean> getIsSaving() { return isSaving; }
    public LiveData<String> getSaveSuccess() { return saveSuccess; }
    public LiveData<String> getSaveError() { return saveError; }

    public void updateProfile(String name, String phone, String birthdate) {
        isSaving.setValue(true);
        String userId = "1";
        UpdateProfileRequest request = new UpdateProfileRequest(name, phone, birthdate);

        userRepository.updateProfile(getApplication(), userId, request, new UserRepository.UpdateCallback() {
            @Override
            public void onSuccess() {
                isSaving.postValue(false);
                saveSuccess.postValue("Perfil actualizado correctamente");
            }
            @Override
            public void onError(String error) {
                isSaving.postValue(false);
                saveError.postValue(error);
            }
        });
    }

    public void linkMembership(String membershipKey) {
        isSaving.setValue(true);
        userRepository.linkMembership(getApplication(), membershipKey, new UserRepository.UpdateCallback() {
            @Override
            public void onSuccess() {
                isSaving.postValue(false);
                saveSuccess.postValue("Membresía vinculada correctamente");
            }
            @Override
            public void onError(String error) {
                isSaving.postValue(false);
                saveError.postValue(error);
            }
        });
    }
}