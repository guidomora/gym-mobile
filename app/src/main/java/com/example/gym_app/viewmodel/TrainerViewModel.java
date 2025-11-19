package com.example.gym_app.viewmodel;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.gym_app.data.RoutineRepository;
import com.example.gym_app.model.Routine;

import java.util.ArrayList;
import java.util.List;

public class TrainerViewModel extends ViewModel {

    private final RoutineRepository routineRepository;

    private final MutableLiveData<List<Routine>> routinesState = new MutableLiveData<>();
    private final MutableLiveData<String> errorState = new MutableLiveData<>();
    private final MutableLiveData<String> deleteMessageState = new MutableLiveData<>();
    private final MutableLiveData<Boolean> emptyState = new MutableLiveData<>();

    public TrainerViewModel() {
        this.routineRepository = new RoutineRepository();
    }

    public LiveData<List<Routine>> getRoutinesState() { return routinesState; }
    public LiveData<String> getErrorState() { return errorState; }
    public LiveData<String> getDeleteMessageState() { return deleteMessageState; }
    public LiveData<Boolean> getEmptyState() { return emptyState; }

    public void loadRoutines(Context context, List<String> routineIds) {
        if (routineIds == null || routineIds.isEmpty()) {
            routinesState.setValue(new ArrayList<>());
            emptyState.setValue(true);
            return;
        }

        try {
            List<Routine> routines = routineRepository.getRoutinesByIds(context, routineIds);

            routinesState.setValue(routines);
            emptyState.setValue(routines.isEmpty());

        } catch (Exception e) {
            errorState.setValue("Error al cargar rutinas: " + e.getMessage());
        }
    }

    public void deleteRoutine(Context context, Routine routine) {
        if (routine == null) return;

        boolean success = routineRepository.deleteRoutine(context, routine.getId());

        if (success) {
            List<Routine> currentList = routinesState.getValue();
            if (currentList != null) {
                List<Routine> updatedList = new ArrayList<>(currentList);
                for (int i = 0; i < updatedList.size(); i++) {
                    if (updatedList.get(i).getId().equals(routine.getId())) {
                        updatedList.remove(i);
                        break;
                    }
                }
                routinesState.setValue(updatedList);
                emptyState.setValue(updatedList.isEmpty());
            }
            deleteMessageState.setValue("Rutina eliminada correctamente");
        } else {
            errorState.setValue("No se pudo eliminar la rutina");
        }
    }
}