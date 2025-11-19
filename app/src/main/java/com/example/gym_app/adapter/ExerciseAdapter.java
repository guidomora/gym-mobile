package com.example.gym_app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gym_app.R;
import com.example.gym_app.model.Exercise;

import java.util.Objects;

public class ExerciseAdapter extends ListAdapter<Exercise, ExerciseAdapter.ExerciseViewHolder> {

    private static final DiffUtil.ItemCallback<Exercise> DIFF_CALLBACK = new DiffUtil.ItemCallback<Exercise>() {
        @Override
        public boolean areItemsTheSame(@NonNull Exercise oldItem, @NonNull Exercise newItem) {
            return Objects.equals(oldItem.getName(), newItem.getName())
                    && Objects.equals(oldItem.getSetsReps(), newItem.getSetsReps());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Exercise oldItem, @NonNull Exercise newItem) {
            return Objects.equals(oldItem.getName(), newItem.getName())
                    && Objects.equals(oldItem.getSetsReps(), newItem.getSetsReps())
                    && Objects.equals(oldItem.getRest(), newItem.getRest());
        }
    };

    public ExerciseAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise_card, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        holder.bind(getItem(position), position == 0);
    }

    static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final TextView setsRepsTextView;
        private final TextView restTextView;

        ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.tv_exercise_name);
            setsRepsTextView = itemView.findViewById(R.id.tv_exercise_sets_reps);
            restTextView = itemView.findViewById(R.id.tv_exercise_rest);
        }

        void bind(Exercise exercise, boolean isFirstItem) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) itemView.getLayoutParams();
            if (params != null) {
                params.topMargin = isFirstItem ? 0 : itemView.getResources().getDimensionPixelSize(R.dimen.exercise_item_spacing);
                itemView.setLayoutParams(params);
            }
            nameTextView.setText(exercise.getName());
            setsRepsTextView.setText(exercise.getSetsReps());
            restTextView.setText(exercise.getRest());
        }
    }
}