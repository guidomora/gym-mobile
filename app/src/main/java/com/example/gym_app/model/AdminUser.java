package com.example.gym_app.model;

public class AdminUser {

    private final String name;
    private String role;

    public AdminUser(String name, String role) {
        this.name = name;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
