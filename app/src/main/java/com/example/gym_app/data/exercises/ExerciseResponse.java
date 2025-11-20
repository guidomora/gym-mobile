
package com.example.gym_app.data.exercises;

import com.google.gson.annotations.SerializedName;

public class ExerciseResponse {

    @SerializedName("id")
    private Long id;

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

    public Long getId() { return id; }
    public String getName() { return name; }
    public Integer getSets() { return sets; }
    public Integer getRepetitions() { return repetitions; }
    public Integer getRestTime() { return restTime; }
    public String getWeightType() { return weightType; }
    public Long getRoutineId() { return routineId; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setSets(Integer sets) { this.sets = sets; }
    public void setRepetitions(Integer repetitions) { this.repetitions = repetitions; }
    public void setRestTime(Integer restTime) { this.restTime = restTime; }
    public void setWeightType(String weightType) { this.weightType = weightType; }
    public void setRoutineId(Long routineId) { this.routineId = routineId; }
}