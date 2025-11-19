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
    private String name;
    private String series;
    private String repetitions;
    private String rest;
    private String suggestedWeight;

    public EditableExercise() {
        this(UUID.randomUUID().toString(), "", "", "", "", "");
    }

    public EditableExercise(String id, String name, String series, String repetitions,
                            String rest, String suggestedWeight) {
        this.id = id == null ? UUID.randomUUID().toString() : id;
        this.name = valueOrEmpty(name);
        this.series = valueOrEmpty(series);
        this.repetitions = valueOrEmpty(repetitions);
        this.rest = valueOrEmpty(rest);
        this.suggestedWeight = valueOrEmpty(suggestedWeight);
    }

    public static EditableExercise fromExercise(@Nullable Exercise exercise) {
        if (exercise == null) {
            return new EditableExercise();
        }
        String name = valueOrEmpty(exercise.getName());
        String rest = valueOrEmpty(exercise.getRest());
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
        return new EditableExercise(UUID.randomUUID().toString(), name, series, repetitions, rest, "");
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

        return new Exercise(name, setsRepsBuilder.toString().trim(), rest);
    }

    public String getId() {
        return id;
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