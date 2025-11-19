package com.example.gym_app.model;

import java.util.Collections;
import java.util.List;

public class Routine {
    private final String id;
    private final String name;
    private final int durationInMinutes;
    private final String dayOfWeek;
    private final List<Exercise> exercises;

    public Routine(String id, String name, int durationInMinutes, String dayOfWeek, List<Exercise> exercises) {
        this.id = id;
        this.name = name;
        this.durationInMinutes = durationInMinutes;
        this.dayOfWeek = dayOfWeek;
        this.exercises = exercises == null ? Collections.emptyList() : Collections.unmodifiableList(exercises);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getDurationInMinutes() {
        return durationInMinutes;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public List<Exercise> getExercises() {
        return exercises;
    }
}