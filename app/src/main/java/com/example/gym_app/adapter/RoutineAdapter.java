package com.example.gym_app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gym_app.R;
import com.example.gym_app.model.Routine;

import java.util.ArrayList;
import java.util.List;

public class RoutineAdapter extends RecyclerView.Adapter<RoutineAdapter.RoutineViewHolder> {

    private final List<Routine> routines = new ArrayList<>();

    @NonNull
    @Override
    public RoutineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_routine_card, parent, false);
        return new RoutineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoutineViewHolder holder, int position) {
        Routine routine = routines.get(position);
        holder.bind(routine);
    }

    @Override
    public int getItemCount() {
        return routines.size();
    }

    public void submitList(List<Routine> newRoutines) {
        routines.clear();
        if (newRoutines != null) {
            routines.addAll(newRoutines);
        }
        notifyDataSetChanged();
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