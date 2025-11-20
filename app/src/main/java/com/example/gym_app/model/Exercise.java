package com.example.gym_app.model;

public class Exercise {
    private final String name;
    private final String setsReps;
    private final String rest;

    public Exercise(String name, String setsReps, String rest) {
        this.name = name;
        this.setsReps = setsReps;
        this.rest = rest;
    }

    public String getName() {
        return name;
    }

    public String getSetsReps() {
        return setsReps;
    }

    public String getRest() {
        return rest;
    }
}