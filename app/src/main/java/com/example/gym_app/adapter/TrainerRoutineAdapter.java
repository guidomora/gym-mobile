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

import java.util.Objects;

public class TrainerRoutineAdapter extends ListAdapter<Routine, TrainerRoutineAdapter.RoutineViewHolder> {

    public interface OnRoutineSelectedListener {
        void onRoutineSelected(Routine routine);
    }

    private static final DiffUtil.ItemCallback<Routine> DIFF_CALLBACK = new DiffUtil.ItemCallback<Routine>() {
        @Override
        public boolean areItemsTheSame(@NonNull Routine oldItem, @NonNull Routine newItem) {
            if (!oldItem.getId().isEmpty() || !newItem.getId().isEmpty()) {
                return Objects.equals(oldItem.getId(), newItem.getId());
            }
            return Objects.equals(oldItem.getName(), newItem.getName())
                    && Objects.equals(oldItem.getDayOfWeek(), newItem.getDayOfWeek());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Routine oldItem, @NonNull Routine newItem) {
            return Objects.equals(oldItem.getId(), newItem.getId())
                    && Objects.equals(oldItem.getName(), newItem.getName())
                    && Objects.equals(oldItem.getDayOfWeek(), newItem.getDayOfWeek())
                    && oldItem.getDurationInMinutes() == newItem.getDurationInMinutes();
        }
    };

    private final OnRoutineSelectedListener onRoutineSelectedListener;

    public TrainerRoutineAdapter(OnRoutineSelectedListener onRoutineSelectedListener) {
        super(DIFF_CALLBACK);
        this.onRoutineSelectedListener = onRoutineSelectedListener;
    }

    @NonNull
    @Override
    public RoutineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_trainer_routine, parent, false);
        return new RoutineViewHolder(view, onRoutineSelectedListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RoutineViewHolder holder, int position) {
        Routine routine = getItem(position);
        holder.bind(routine);
    }

    static class RoutineViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final TextView metaTextView;
        private Routine routine;
        private final OnRoutineSelectedListener onRoutineSelectedListener;

        RoutineViewHolder(@NonNull View itemView, OnRoutineSelectedListener onRoutineSelectedListener) {
            super(itemView);
            this.onRoutineSelectedListener = onRoutineSelectedListener;
            nameTextView = (TextView) itemView.findViewById(R.id.tv_trainer_name);
            metaTextView = (TextView) itemView.findViewById(R.id.tv_routine_meta);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (routine != null && RoutineViewHolder.this.onRoutineSelectedListener != null) {
                        RoutineViewHolder.this.onRoutineSelectedListener.onRoutineSelected(routine);
                    }
                }
            });
        }

        void bind(Routine routine) {
            this.routine = routine;
            nameTextView.setText(routine.getName());
            metaTextView.setText(buildMetaText(routine));
        }

        private String buildMetaText(Routine routine) {
            String day = routine.getDayOfWeek();
            if (day == null || day.isEmpty()) {
                return itemView.getContext().getString(R.string.routine_meta_only_duration, routine.getDurationInMinutes());
            }
            return itemView.getContext().getString(R.string.routine_meta_format, routine.getDurationInMinutes(), day);
        }
    }
}