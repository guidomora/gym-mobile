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
import com.example.gym_app.model.Routine;

import java.util.ArrayList;
import java.util.List;

import java.util.Objects;

public class RoutineAdapter extends ListAdapter<Routine, RoutineAdapter.RoutineViewHolder> {

    private static final DiffUtil.ItemCallback<Routine> DIFF_CALLBACK = new DiffUtil.ItemCallback<Routine>() {
        @Override
        public boolean areItemsTheSame(@NonNull Routine oldItem, @NonNull Routine newItem) {
            return Objects.equals(oldItem.getName(), newItem.getName())
                    && Objects.equals(oldItem.getDayOfWeek(), newItem.getDayOfWeek());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Routine oldItem, @NonNull Routine newItem) {
            return Objects.equals(oldItem.getName(), newItem.getName())
                    && Objects.equals(oldItem.getDayOfWeek(), newItem.getDayOfWeek())
                    && oldItem.getDurationInMinutes() == newItem.getDurationInMinutes();
        }
    };

    public RoutineAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public RoutineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_routine_card, parent, false);
        return new RoutineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoutineViewHolder holder, int position) {
        Routine routine = getItem(position);
        holder.bind(routine);
    }

    static class RoutineViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final TextView metaTextView;

        RoutineViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.tv_routine_name);
            metaTextView = itemView.findViewById(R.id.tv_routine_meta);
        }

        void bind(Routine routine) {
            nameTextView.setText(routine.getName());
            String meta = itemView.getContext().getString(R.string.routine_meta_format, routine.getDurationInMinutes(), routine.getDayOfWeek());
            metaTextView.setText(meta);
        }
    }
}