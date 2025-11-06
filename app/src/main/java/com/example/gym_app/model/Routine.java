package com.example.gym_app.model;

public class Routine {
    private final String name;
    private final int durationInMinutes;
    private final String dayOfWeek;

    public Routine(String name, int durationInMinutes, String dayOfWeek) {
        this.name = name;
        this.durationInMinutes = durationInMinutes;
        this.dayOfWeek = dayOfWeek;
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
}