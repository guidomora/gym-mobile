package com.example.gym_app.data.users;

public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private String role; // Recibiremos el Enum como String ("STUDENT", "TRAINER")
    private String gymName;

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public String getGymName() { return gymName; }
}
