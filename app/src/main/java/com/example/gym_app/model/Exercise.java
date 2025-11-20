package com.example.gym_app.model;

public class Exercise {
    private final Long id;
    private final String name;
    private final String setsReps;
    private final String rest;
    private final Integer sets;
    private final Integer repetitions;
    private final Integer restTime;
    private final String weightType;
    private final Long routineId;

    public Exercise(String name, String setsReps, String rest) {
        this(null, name, null, null, null, null, null, setsReps, rest);
    }

    public Exercise(Long id,
                    String name,
                    Integer sets,
                    Integer repetitions,
                    Integer restTime,
                    String weightType,
                    Long routineId) {
        this(id, name, sets, repetitions, restTime, weightType, routineId,
                buildSetsReps(sets, repetitions), buildRest(restTime));
    }

    public Exercise(Long id,
                    String name,
                    Integer sets,
                    Integer repetitions,
                    Integer restTime,
                    String weightType,
                    Long routineId,
                    String setsReps,
                    String rest) {
        this.id = id;
        this.name = name;
        this.sets = sets;
        this.repetitions = repetitions;
        this.restTime = restTime;
        this.weightType = weightType;
        this.routineId = routineId;
        this.setsReps = setsReps == null ? "" : setsReps;
        this.rest = rest == null ? "" : rest;
    }

    private static String buildSetsReps(Integer sets, Integer repetitions) {
        StringBuilder setsRepsBuilder = new StringBuilder();
        if (sets != null && sets > 0) {
            setsRepsBuilder.append(sets).append(" series");
            if (repetitions != null && repetitions > 0) {
                setsRepsBuilder.append(" x ").append(repetitions).append(" repeticiones");
            }
        } else if (repetitions != null && repetitions > 0) {
            setsRepsBuilder.append(repetitions).append(" repeticiones");
        }
        return setsRepsBuilder.toString().trim();
    }

    private static String buildRest(Integer restTime) {
        if (restTime == null) {
            return "";
        }
        return restTime + " seg";
    }

    public Long getId() {
        return id;
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

    public Integer getSets() {
        return sets;
    }

    public Integer getRepetitions() {
        return repetitions;
    }

    public Integer getRestTime() {
        return restTime;
    }

    public String getWeightType() {
        return weightType;
    }

    public Long getRoutineId() {
        return routineId;
    }
}