package com.example.gym_app.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrainerStudent {
    private final String id;
    private final String fullName;
    private final List<String> routineIds;

    public TrainerStudent(String id, String fullName) {
        this(id, fullName, Collections.<String>emptyList());
    }

    public TrainerStudent(String id, String fullName, List<String> routineIds) {
        this.id = id;
        this.fullName = fullName;
        if (routineIds == null) {
            this.routineIds = Collections.emptyList();
        } else {
            this.routineIds = Collections.unmodifiableList(new ArrayList<>(routineIds));
        }
    }

    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public List<String> getRoutineIds() {
        return new ArrayList<>(routineIds);
    }
}