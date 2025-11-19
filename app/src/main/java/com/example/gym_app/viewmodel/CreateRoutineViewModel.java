package com.example.gym_app.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.gym_app.data.RoutineRepository;
import com.example.gym_app.data.routines.CreateRoutineRequest;
import com.example.gym_app.data.routines.UpdateRoutineRequest;

public class CreateRoutineViewModel extends AndroidViewModel {

    private final RoutineRepository routineRepository;

    private final MutableLiveData<Boolean> isSaving = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public CreateRoutineViewModel(@NonNull Application application) {
        super(application);
        this.routineRepository = new RoutineRepository();
    }

    public LiveData<Boolean> getIsSaving() { return isSaving; }
    public LiveData<String> getSuccessMessage() { return successMessage; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    public void createRoutine(CreateRoutineRequest request) {
        isSaving.setValue(true);

        routineRepository.createRoutine(getApplication(), request, new RoutineRepository.CreateRoutineCallback() {
            @Override
            public void onSuccess() {
                isSaving.postValue(false);
                successMessage.postValue("Rutina creada exitosamente");
            }

            @Override
            public void onError(@NonNull String error) {
                isSaving.postValue(false);
                errorMessage.postValue(error);
            }
        });
    }

    public void updateRoutine(Long routineId, UpdateRoutineRequest request) {
        isSaving.setValue(true);

        routineRepository.updateRoutine(getApplication(), routineId, request, new RoutineRepository.UpdateRoutineCallback() {
            @Override
            public void onSuccess() {
                isSaving.postValue(false);
                successMessage.postValue("Rutina actualizada correctamente");
            }

            @Override
            public void onError(@NonNull String error) {
                isSaving.postValue(false);
                errorMessage.postValue(error);
            }
        });
    }
}