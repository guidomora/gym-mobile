package com.example.gym_app.viewmodel;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.gym_app.data.RoutineRepository;
import com.example.gym_app.data.routines.CreateRoutineRequest;

public class CreateRoutineViewModel extends ViewModel {

    private final RoutineRepository routineRepository;

    private final MutableLiveData<Boolean> isSaving = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public CreateRoutineViewModel() {
        this.routineRepository = new RoutineRepository();
    }

    public LiveData<Boolean> getIsSaving() { return isSaving; }
    public LiveData<String> getSuccessMessage() { return successMessage; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    public void createRoutine(Context context, CreateRoutineRequest request) {
        isSaving.setValue(true);


        routineRepository.createRoutine(context, request, new RoutineRepository.CreateRoutineCallback() {
            @Override
            public void onSuccess() {
                isSaving.postValue(false);
                successMessage.postValue("Rutina creada exitosamente");
            }

            @Override
            public void onError(String error) {
                isSaving.postValue(false);
                errorMessage.postValue(error);
            }
        });
    }
}