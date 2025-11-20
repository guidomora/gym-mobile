package com.example.gym_app.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents an editable version of an {@link Exercise}. It keeps the current state in memory so it
 * can later be transformed again into a regular immutable {@link Exercise} instance when the
 * backend integration is ready.
 */
public class EditableExercise {

    private static final Pattern SETS_AND_REPS_PATTERN =
            Pattern.compile("(\\d+)\\s*series\\s*x\\s*(.+)", Pattern.CASE_INSENSITIVE);

    private final String id;
    @Nullable
    private Long backendId;
    private String name;
    private String series;
    private String repetitions;
    private String rest;
    private String suggestedWeight;
    private String weightType;

    public EditableExercise() {
        this(UUID.randomUUID().toString(), null, "", "", "", "", "", "");
    }

    public EditableExercise(String id, Long backendId, String name, String series, String repetitions,
                            String rest, String suggestedWeight, String weightType) {
        this.id = id == null ? UUID.randomUUID().toString() : id;
        this.backendId = backendId;
        this.name = valueOrEmpty(name);
        this.series = valueOrEmpty(series);
        this.repetitions = valueOrEmpty(repetitions);
        this.rest = valueOrEmpty(rest);
        this.suggestedWeight = valueOrEmpty(suggestedWeight);
        this.weightType = valueOrEmpty(weightType);
    }

    public static EditableExercise fromExercise(@Nullable Exercise exercise) {
        if (exercise == null) {
            return new EditableExercise();
        }
        String name = valueOrEmpty(exercise.getName());
        String rest = exercise.getRestTime() == null
                ? valueOrEmpty(exercise.getRest())
                : String.valueOf(exercise.getRestTime());
        String setsReps = valueOrEmpty(exercise.getSetsReps());

        String series = "";
        String repetitions = "";
        if (!setsReps.isEmpty()) {
            Matcher matcher = SETS_AND_REPS_PATTERN.matcher(setsReps);
            if (matcher.find()) {
                series = matcher.group(1);
                repetitions = cleanSuffix(matcher.group(2));
            } else {
                series = setsReps;
            }
        }
        Long remoteId = exercise.getId();
        String weightType = valueOrEmpty(exercise.getWeightType());
        return new EditableExercise(UUID.randomUUID().toString(), remoteId, name, series, repetitions, rest, "", weightType);
    }

    private static String cleanSuffix(String value) {
        String sanitized = valueOrEmpty(value);
        sanitized = sanitized.replaceAll("(?i)repeticiones", "");
        return sanitized.trim();
    }

    private static String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }

    public Exercise toExercise() {
        StringBuilder setsRepsBuilder = new StringBuilder();
        if (!series.isEmpty()) {
            setsRepsBuilder.append(series.trim());
            if (!repetitions.isEmpty()) {
                setsRepsBuilder.append(" series x ").append(repetitions.trim());
                if (!repetitions.toLowerCase(Locale.ROOT).contains("repet")) {
                    setsRepsBuilder.append(" repeticiones");
                }
            } else {
                setsRepsBuilder.append(" series");
            }
        } else if (!repetitions.isEmpty()) {
            setsRepsBuilder.append(repetitions.trim());
        }

        Integer restTime = parseIntegerOrNull(rest);
        Integer setsInt = parseIntegerOrNull(series);
        Integer repetitionsInt = parseIntegerOrNull(repetitions);
        return new Exercise(backendId,
                name,
                setsInt,
                repetitionsInt,
                restTime,
                weightType,
                null,
                setsRepsBuilder.toString().trim(),
                restTime == null ? rest : restTime + " seg");
    }

    @Nullable
    private Integer parseIntegerOrNull(String value) {
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception exception) {
            return null;
        }
    }

    public String getId() {
        return id;
    }

    @Nullable
    public Long getBackendId() {
        return backendId;
    }

    public void setBackendId(@Nullable Long backendId) {
        this.backendId = backendId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = valueOrEmpty(name);
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = valueOrEmpty(series);
    }

    public String getRepetitions() {
        return repetitions;
    }

    public void setRepetitions(String repetitions) {
        this.repetitions = valueOrEmpty(repetitions);
    }

    public String getRest() {
        return rest;
    }

    public void setRest(String rest) {
        this.rest = valueOrEmpty(rest);
    }

    public String getSuggestedWeight() {
        return suggestedWeight;
    }

    public void setSuggestedWeight(String suggestedWeight) {
        this.suggestedWeight = valueOrEmpty(suggestedWeight);
    }

    public String getWeightType() {
        return weightType;
    }

    public void setWeightType(String weightType) {
        this.weightType = valueOrEmpty(weightType);
    }

    @NonNull
    @Override
    public String toString() {
        return "EditableExercise{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", series='" + series + '\'' +
                ", repetitions='" + repetitions + '\'' +
                ", rest='" + rest + '\'' +
                ", suggestedWeight='" + suggestedWeight + '\'' +
                '}';
    }
}