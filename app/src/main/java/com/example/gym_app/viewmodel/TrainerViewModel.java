package com.example.gym_app.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.gym_app.data.RoutineRepository;
import com.example.gym_app.model.Routine;

import java.util.ArrayList;
import java.util.List;

public class TrainerViewModel extends AndroidViewModel {

    private final RoutineRepository routineRepository;

    private final MutableLiveData<List<Routine>> routinesState = new MutableLiveData<>();
    private final MutableLiveData<String> errorState = new MutableLiveData<>();
    private final MutableLiveData<String> deleteMessageState = new MutableLiveData<>();
    private final MutableLiveData<Boolean> emptyState = new MutableLiveData<>();

    public TrainerViewModel(@NonNull Application application) {
        super(application);
        this.routineRepository = new RoutineRepository();
    }

    public LiveData<List<Routine>> getRoutinesState() { return routinesState; }
    public LiveData<String> getErrorState() { return errorState; }
    public LiveData<String> getDeleteMessageState() { return deleteMessageState; }
    public LiveData<Boolean> getEmptyState() { return emptyState; }

    public void loadRoutines(List<String> routineIds) {
        if (routineIds == null || routineIds.isEmpty()) {
            routinesState.setValue(new ArrayList<>());
            emptyState.setValue(true);
            return;
        }

        try {
            List<Routine> routines = routineRepository.getRoutinesByIds(getApplication(), routineIds);
            routinesState.setValue(routines);
            emptyState.setValue(routines.isEmpty());
        } catch (Exception e) {
            errorState.setValue("Error al cargar rutinas: " + e.getMessage());
        }
    }

    public void loadRoutinesByStudentId(String studentIdStr) {
        try {
            Long studentId = Long.parseLong(studentIdStr);
            routineRepository.getRoutinesByUserId(getApplication(), studentId, new RoutineRepository.GetAllRoutinesCallback() {
                @Override
                public void onSuccess(List<Routine> routines) {
                    routinesState.postValue(routines);
                    emptyState.postValue(routines == null || routines.isEmpty());
                }

                @Override
                public void onError(@NonNull String errorMessage) {
                    errorState.postValue(errorMessage);
                }
            });
        } catch (NumberFormatException e) {
            errorState.setValue("ID de estudiante inválido");
        }
    }

    public void deleteRoutine(Routine routine) {
        if (routine == null) return;

        try {
            Long routineId = Long.parseLong(routine.getId());

            routineRepository.deleteRoutine(getApplication(), routineId, new RoutineRepository.DeleteRoutineCallback() {
                @Override
                public void onSuccess() {
                    List<Routine> currentList = routinesState.getValue();
                    if (currentList != null) {
                        List<Routine> updatedList = new ArrayList<>(currentList);
                        for (int i = 0; i < updatedList.size(); i++) {
                            if (updatedList.get(i).getId().equals(routine.getId())) {
                                updatedList.remove(i);
                                break;
                            }
                        }
                        routinesState.postValue(updatedList);
                        emptyState.postValue(updatedList.isEmpty());
                    }
                    deleteMessageState.postValue("Rutina eliminada correctamente");
                }

                @Override
                public void onError(@NonNull String errorMessage) {
                    errorState.postValue(errorMessage);
                }
            });
        } catch (NumberFormatException e) {
            errorState.setValue("No se pudo borrar: ID de rutina inválido");
        }
    }
}