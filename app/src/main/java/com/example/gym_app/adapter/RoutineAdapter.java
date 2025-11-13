package com.example.gym_app.adapter;

import android.text.TextUtils;
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

public class RoutineAdapter extends ListAdapter<Routine, RoutineAdapter.RoutineViewHolder> {

    public interface OnRoutineClickListener {
        void onRoutineClick(Routine routine);
    }

    private static final DiffUtil.ItemCallback<Routine> DIFF_CALLBACK = new DiffUtil.ItemCallback<Routine>() {
        @Override
        public boolean areItemsTheSame(@NonNull Routine oldItem, @NonNull Routine newItem) {
            if (!TextUtils.isEmpty(oldItem.getId()) || !TextUtils.isEmpty(newItem.getId())) {
                return TextUtils.equals(oldItem.getId(), newItem.getId());
            }
            return Objects.equals(oldItem.getName(), newItem.getName())
                    && Objects.equals(oldItem.getDayOfWeek(), newItem.getDayOfWeek());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Routine oldItem, @NonNull Routine newItem) {
            return TextUtils.equals(oldItem.getId(), newItem.getId())
                    && Objects.equals(oldItem.getName(), newItem.getName())
                    && Objects.equals(oldItem.getDayOfWeek(), newItem.getDayOfWeek())
                    && oldItem.getDurationInMinutes() == newItem.getDurationInMinutes();
        }
    };

    private final OnRoutineClickListener onRoutineClickListener;

    public RoutineAdapter(OnRoutineClickListener onRoutineClickListener) {
        super(DIFF_CALLBACK);
        this.onRoutineClickListener = onRoutineClickListener;
    }

    @NonNull
    @Override
    public RoutineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_routine_card, parent, false);
        return new RoutineViewHolder(view, onRoutineClickListener);
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

        RoutineViewHolder(@NonNull View itemView, OnRoutineClickListener onRoutineClickListener) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.tv_routine_name);
            metaTextView = itemView.findViewById(R.id.tv_routine_meta);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (routine != null && onRoutineClickListener != null) {
                        onRoutineClickListener.onRoutineClick(routine);
                    }
                }
            });
        }

        void bind(Routine routine) {
            this.routine = routine;
            nameTextView.setText(routine.getName());
            String meta = buildMetaText(routine);
            metaTextView.setText(meta);
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