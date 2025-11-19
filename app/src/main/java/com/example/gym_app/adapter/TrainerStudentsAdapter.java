package com.example.gym_app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.example.gym_app.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gym_app.model.TrainerStudent;

import java.util.ArrayList;
import java.util.List;

public class TrainerStudentsAdapter extends RecyclerView.Adapter<TrainerStudentsAdapter.StudentViewHolder> {

    public interface OnStudentClickListener {
        void onStudentSelected(TrainerStudent student);
    }

    private final List<TrainerStudent> students = new ArrayList<>();
    private final OnStudentClickListener listener;

    public TrainerStudentsAdapter(List<TrainerStudent> students, OnStudentClickListener listener) {
        if (students != null) {
            this.students.addAll(students);
        }
        this.listener = listener;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_trainer_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        final TrainerStudent student = students.get(position);
        holder.studentName.setText(student.getFullName());

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onStudentSelected(student);
                }
            }
        };

        holder.itemView.setOnClickListener(clickListener);
        holder.viewButton.setOnClickListener(clickListener);
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    static class StudentViewHolder extends RecyclerView.ViewHolder {
        final TextView studentName;
        final Button viewButton;

        StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            studentName = itemView.findViewById(R.id.studentName);
            viewButton = itemView.findViewById(R.id.btn_view_student);
        }
    }
}