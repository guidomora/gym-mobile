package com.example.gym_app.data.routines;

import com.google.gson.annotations.SerializedName;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;
public class RoutineResponse {
    @SerializedName("id")
    private Long id;

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

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDayOfWeek() { return dayOfWeek; }
    public String getDate() { return date; }
    public Long getStudentId() { return studentId; }
    public Set<Long> getExerciseIds() { return exerciseIds; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public void setDate(String date) { this.date = date; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public void setExerciseIds(Set<Long> exerciseIds) { this.exerciseIds = exerciseIds; }
}