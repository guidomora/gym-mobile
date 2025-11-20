package com.example.gym_app.model;

import com.example.gym_app.model.other.Membership;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;

public class PerfilUser {
    @SerializedName("name")
    private String name;
    @SerializedName("phoneNumber")
    private String phoneNumber;
    @SerializedName("email")
    private String email;
    @SerializedName("birthdate")
    private LocalDate birthdate;
    @SerializedName("membership")
    private com.example.gym_app.model.other.Membership membership;
    public PerfilUser() {}
    public PerfilUser(String name, String phoneNumber, String email, LocalDate birthdate, Membership membership) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.birthdate = birthdate;
        this.membership = membership;
    }
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public String getPhoneNumber() {return phoneNumber;}
    public void setPhoneNumber(String phoneNumber) {this.phoneNumber = phoneNumber;}
    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}
    public LocalDate getBirthdate() {return birthdate;}
    public void setBirthdate(LocalDate birthdate) {this.birthdate = birthdate;}
    public Membership getMembership() {return membership;}
    public void setMembership(Membership membership) {this.membership = membership;}

}
