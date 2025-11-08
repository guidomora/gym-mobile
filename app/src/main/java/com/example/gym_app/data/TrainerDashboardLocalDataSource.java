package com.example.gym_app.data;

import com.example.gym_app.model.TrainerStudent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrainerDashboardLocalDataSource {
    private final String trainerName;
    private final List<TrainerStudent> students;

    public TrainerDashboardLocalDataSource(String trainerName, List<TrainerStudent> students) {
        this.trainerName = trainerName;
        if (students == null) {
            this.students = Collections.emptyList();
        } else {
            this.students = new ArrayList<>(students);
        }
    }

    public String getTrainerName() {
        return trainerName;
    }

    public List<TrainerStudent> getStudents() {
        return new ArrayList<>(students);
    }
}