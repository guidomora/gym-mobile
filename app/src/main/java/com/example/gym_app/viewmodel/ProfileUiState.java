package com.example.gym_app.viewmodel;

import com.example.gym_app.model.PerfilUser;

public abstract class ProfileUiState {

    private ProfileUiState(){}

    public static class Loading extends ProfileUiState {
        public Loading() { super(); }
    }

    public static class Success extends ProfileUiState {
        private final PerfilUser perfilUser;
        public Success(PerfilUser perfilUser) {
            super();
            this.perfilUser = perfilUser;
        }
        public PerfilUser getPerfilUser() { return perfilUser; }
    }

    public static class Error extends ProfileUiState {
        private final String message;
        public Error(String message) {
            super();
            this.message=message;
        }
        public String getMessage() { return message; }
    }
}
