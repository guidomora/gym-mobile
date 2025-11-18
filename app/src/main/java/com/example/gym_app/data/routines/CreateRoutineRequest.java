package com.example.gym_app.data.routines;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CreateRoutineRequest {

    private final String name;
    private final String dayOfWeek;
    private final String date;
    private final String studentId;
    private final List<Long> exerciseIds;

    public CreateRoutineRequest(String name,
                                String dayOfWeek,
                                String date,
                                String studentId,
                                List<Long> exerciseIds) {
        this.name = name;
        this.dayOfWeek = dayOfWeek;
        this.date = date;
        this.studentId = studentId;
        if (exerciseIds == null) {
            this.exerciseIds = Collections.emptyList();
        } else {
            this.exerciseIds = Collections.unmodifiableList(new ArrayList<>(exerciseIds));
        }
    }

    public String getName() {
        return name;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public String getDate() {
        return date;
    }

    public String getStudentId() {
        return studentId;
    }

    public List<Long> getExerciseIds() {
        return exerciseIds;
    }
}
