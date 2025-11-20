package com.example.gym_app.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gym_app.R;
import com.example.gym_app.model.EditableExercise;

import java.util.ArrayList;
import java.util.List;

public class EditableExerciseAdapter extends RecyclerView.Adapter<EditableExerciseAdapter.EditableExerciseViewHolder> {

    public interface OnExerciseListChangedListener {
        void onExerciseListChanged(int itemCount);
    }

    private final List<EditableExercise> exercises = new ArrayList<>();
    private final OnExerciseListChangedListener onExerciseListChangedListener;

    public EditableExerciseAdapter(OnExerciseListChangedListener onExerciseListChangedListener) {
        this.onExerciseListChangedListener = onExerciseListChangedListener;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public EditableExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_editable_exercise, parent, false);
        return new EditableExerciseViewHolder(view, this::removeExerciseAt);
    }

    @Override
    public void onBindViewHolder(@NonNull EditableExerciseViewHolder holder, int position) {
        holder.bind(exercises.get(position));
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    @Override
    public long getItemId(int position) {
        return exercises.get(position).getId().hashCode();
    }

    public void setExercises(List<EditableExercise> newExercises) {
        exercises.clear();
        if (newExercises != null) {
            exercises.addAll(newExercises);
        }
        notifyDataSetChanged();
        notifyListChanged();
    }

    public void addExercise(EditableExercise exercise) {
        if (exercise == null) {
            return;
        }
        exercises.add(exercise);
        notifyItemInserted(exercises.size() - 1);
        notifyListChanged();
    }

    public void removeExerciseAt(int position) {
        if (position < 0 || position >= exercises.size()) {
            return;
        }
        exercises.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, exercises.size() - position);
        notifyListChanged();
    }

    public List<EditableExercise> getExercisesSnapshot() {
        return new ArrayList<>(exercises);
    }

    private void notifyListChanged() {
        if (onExerciseListChangedListener != null) {
            onExerciseListChangedListener.onExerciseListChanged(exercises.size());
        }
    }

    static class EditableExerciseViewHolder extends RecyclerView.ViewHolder {

        private final EditText nameEditText;
        private final EditText seriesEditText;
        private final EditText repetitionsEditText;
        private final EditText restEditText;
        private final Spinner weightTypeSpinner;
        private final ImageButton deleteButton;

        private final ExerciseFieldWatcher nameWatcher;
        private final ExerciseFieldWatcher seriesWatcher;
        private final ExerciseFieldWatcher repetitionsWatcher;
        private final ExerciseFieldWatcher restWatcher;
        private final ArrayAdapter<CharSequence> weightTypeAdapter;
        private EditableExercise currentExercise;
        private boolean ignoreWeightTypeChanges;

        EditableExerciseViewHolder(@NonNull View itemView, RemoveExerciseCallback removeExerciseCallback) {
            super(itemView);
            nameEditText = itemView.findViewById(R.id.et_exercise_name);
            seriesEditText = itemView.findViewById(R.id.et_series);
            repetitionsEditText = itemView.findViewById(R.id.et_repetitions);
            restEditText = itemView.findViewById(R.id.et_rest);
            weightTypeSpinner = itemView.findViewById(R.id.spinner_weight_type);
            deleteButton = itemView.findViewById(R.id.btn_delete_exercise);

            nameWatcher = new ExerciseFieldWatcher((exercise, value) ->
                    exercise.setName(value));
            seriesWatcher = new ExerciseFieldWatcher((exercise, value) ->
                    exercise.setSeries(value));
            repetitionsWatcher = new ExerciseFieldWatcher((exercise, value) ->
                    exercise.setRepetitions(value));
            restWatcher = new ExerciseFieldWatcher((exercise, value) ->
                    exercise.setRest(value));

            nameEditText.addTextChangedListener(nameWatcher);
            seriesEditText.addTextChangedListener(seriesWatcher);
            repetitionsEditText.addTextChangedListener(repetitionsWatcher);
            restEditText.addTextChangedListener(restWatcher);
            weightTypeAdapter = ArrayAdapter.createFromResource(itemView.getContext(),
                    R.array.weight_type_options, android.R.layout.simple_spinner_item);
            weightTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            weightTypeSpinner.setAdapter(weightTypeAdapter);
            weightTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (ignoreWeightTypeChanges || currentExercise == null) {
                        return;
                    }
                    Object selected = parent.getItemAtPosition(position);
                    if (selected != null) {
                        currentExercise.setWeightType(selected.toString());
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // No-op
                }
            });

            deleteButton.setOnClickListener(v -> {
                if (removeExerciseCallback == null) {
                    return;
                }
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    removeExerciseCallback.removeExercise(position);
                }
            });
        }

        void bind(EditableExercise exercise) {
            nameWatcher.bindExercise(exercise);
            seriesWatcher.bindExercise(exercise);
            repetitionsWatcher.bindExercise(exercise);
            restWatcher.bindExercise(exercise);
            currentExercise = exercise;

            nameWatcher.setIgnoreChanges(true);
            seriesWatcher.setIgnoreChanges(true);
            repetitionsWatcher.setIgnoreChanges(true);
            restWatcher.setIgnoreChanges(true);
            ignoreWeightTypeChanges = true;

            nameEditText.setText(exercise.getName());
            seriesEditText.setText(exercise.getSeries());
            repetitionsEditText.setText(exercise.getRepetitions());
            restEditText.setText(exercise.getRest());
            String resolvedWeightType = resolveWeightSelection(exercise.getWeightType());
            int spinnerPosition = weightTypeAdapter.getPosition(resolvedWeightType);
            if (spinnerPosition >= 0) {
                weightTypeSpinner.setSelection(spinnerPosition);
            }
            exercise.setWeightType(resolvedWeightType);

            nameWatcher.setIgnoreChanges(false);
            seriesWatcher.setIgnoreChanges(false);
            repetitionsWatcher.setIgnoreChanges(false);
            restWatcher.setIgnoreChanges(false);
            ignoreWeightTypeChanges = false;
        }

        private String resolveWeightSelection(String weightType) {
            if (weightType == null || weightType.isEmpty()) {
                return "NINGUNA";
            }
            return weightType.toUpperCase();
        }
    }

    private interface RemoveExerciseCallback {
        void removeExercise(int position);
    }

    private static class ExerciseFieldWatcher implements TextWatcher {

        interface ExerciseValueConsumer {
            void consume(EditableExercise exercise, String value);
        }

        private final ExerciseValueConsumer consumer;
        private EditableExercise exercise;
        private boolean ignoreChanges;

        ExerciseFieldWatcher(ExerciseValueConsumer consumer) {
            this.consumer = consumer;
        }

        void bindExercise(EditableExercise exercise) {
            this.exercise = exercise;
        }

        EditableExercise getExercise() {
            return exercise;
        }

        void setIgnoreChanges(boolean ignore) {
            this.ignoreChanges = ignore;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // No-op
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // No-op
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (ignoreChanges || exercise == null) {
                return;
            }
            consumer.consume(exercise, s == null ? "" : s.toString());
        }
    }
}