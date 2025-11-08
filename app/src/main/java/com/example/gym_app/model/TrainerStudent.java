package com.example.gym_app.model;

public class TrainerStudent {
    private final String id;
    private final String fullName;

    public TrainerStudent(String id, String fullName) {
        this.id = id;
        this.fullName = fullName;
    }

    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }
}