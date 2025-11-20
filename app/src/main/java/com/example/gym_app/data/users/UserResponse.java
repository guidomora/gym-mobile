package com.example.gym_app.data.users;

import com.example.gym_app.model.other.Membership;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;

public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private String role;
    private String phoneNumber;
    private LocalDate birthDate;
    private Membership membership;
    private String gymName;

    public UserResponse() {};

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public String getGymName() { return gymName; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public LocalDate getBirthDate() { return birthDate; }
    @SerializedName("memberships")
    public Membership getMembership() { return membership; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setRole(String role) { this.role = role; }
    public void setEmail(String email) {this.email = email;}
    public void setGymName(String gymName) { this.gymName = gymName; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public void setMembership(Membership membership) { this.membership = membership; }

}
