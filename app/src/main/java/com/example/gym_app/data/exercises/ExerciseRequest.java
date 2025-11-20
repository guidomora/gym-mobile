
package com.example.gym_app.data.exercises;

import com.google.gson.annotations.SerializedName;

public class ExerciseRequest {

    @SerializedName("name")
    private String name;

    @SerializedName("sets")
    private Integer sets;

    @SerializedName("repetitions")
    private Integer repetitions;

    @SerializedName("restTime")
    private Integer restTime;

    @SerializedName("weightType")
    private String weightType;

    @SerializedName("routineId")
    private Long routineId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSets() {
        return sets;
    }

    public void setSets(Integer sets) {
        this.sets = sets;
    }

    public Integer getRepetitions() {
        return repetitions;
    }

    public void setRepetitions(Integer repetitions) {
        this.repetitions = repetitions;
    }

    public Integer getRestTime() {
        return restTime;
    }

    public void setRestTime(Integer restTime) {
        this.restTime = restTime;
    }

    public String getWeightType() {
        return weightType;
    }

    public void setWeightType(String weightType) {
        this.weightType = weightType;
    }

    public Long getRoutineId() {
        return routineId;
    }

    public void setRoutineId(Long routineId) {
        this.routineId = routineId;
    }
}