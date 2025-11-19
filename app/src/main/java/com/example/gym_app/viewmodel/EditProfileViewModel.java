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

        // TODO: IMPORTANTE - ID DEL USUARIO
        // Actualmente tu App no guarda el ID numérico del usuario al loguearse (solo guarda el token y email).
        // Para que esto funcione REALMENTE con tu backend, necesitamos ese ID.
        // Por ahora, usamos "1" para probar (ya que es tu usuario de prueba en Postman).
        // *Tarea futura para el equipo:* Guardar el 'id' en SavedLoginData al hacer login.
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
}