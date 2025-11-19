package com.example.gym_app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gym_app.R;
import com.example.gym_app.model.AdminUser;

import java.util.List;

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.AdminUserViewHolder> {

    private final List<AdminUser> users;
    private final LayoutInflater inflater;
    private final ArrayAdapter<CharSequence> roleAdapter;

    public AdminUserAdapter(Context context, List<AdminUser> users) {
        this.users = users;
        this.inflater = LayoutInflater.from(context);
        this.roleAdapter = ArrayAdapter.createFromResource(
                context,
                R.array.user_role_options,
                android.R.layout.simple_spinner_dropdown_item
        );
        this.roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    @NonNull
    @Override
    public AdminUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_admin_user, parent, false);
        return new AdminUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminUserViewHolder holder, int position) {
        AdminUser user = users.get(position);
        holder.bind(user, roleAdapter);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class AdminUserViewHolder extends RecyclerView.ViewHolder {

        private final TextView userNameTextView;
        private final Spinner roleSpinner;

        AdminUserViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            roleSpinner = itemView.findViewById(R.id.roleSpinner);
        }

        void bind(AdminUser user, ArrayAdapter<CharSequence> roleAdapter) {
            userNameTextView.setText(user.getName());
            roleSpinner.setAdapter(roleAdapter);
            setSelectionWithoutTrigger(roleSpinner, roleAdapter, user.getRole());
            roleSpinner.setOnItemSelectedListener(new SimpleItemSelectedListener(selection ->
                    user.setRole(selection)));
        }

        private void setSelectionWithoutTrigger(Spinner spinner, ArrayAdapter<CharSequence> adapter, String role) {
            int position = findRolePosition(adapter, role);
            if (position >= 0) {
                spinner.setSelection(position, false);
            } else {
                spinner.setSelection(0, false);
            }
        }

        private int findRolePosition(ArrayAdapter<CharSequence> adapter, String role) {
            for (int index = 0; index < adapter.getCount(); index++) {
                CharSequence item = adapter.getItem(index);
                if (item != null && item.toString().equalsIgnoreCase(role)) {
                    return index;
                }
            }
            return -1;
        }
    }
}