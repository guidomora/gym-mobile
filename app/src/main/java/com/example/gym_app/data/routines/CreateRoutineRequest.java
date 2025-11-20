package com.example.gym_app.data.routines;

import com.google.gson.annotations.SerializedName;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;

public class CreateRoutineRequest {
    @SerializedName("name")
    private String name;

    @SerializedName("dayOfWeek")
    private String dayOfWeek;

    @SerializedName("date")
    private String date;

    @SerializedName("studentId")
    private Long studentId;

    @SerializedName("exerciseIds")
    private Set<Long> exerciseIds;

    public CreateRoutineRequest(String name, String dayOfWeek, String date, Long studentId, Set<Long> exerciseIds) {
        this.name = name;
        this.dayOfWeek = dayOfWeek;
        this.date = date;
        this.studentId = studentId;
        this.exerciseIds = exerciseIds;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public Set<Long> getExerciseIds() { return exerciseIds; }
    public void setExerciseIds(Set<Long> exerciseIds) { this.exerciseIds = exerciseIds; }
}