package com.example.gym_app.model.other;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;

public class Membership {
    @SerializedName("key")
    private String key;
    @SerializedName("expirationDate")
    private LocalDate expirationDate;

    public Membership() {}
    public Membership(String key, LocalDate expirationDate) {
        this.key = key;
        this.expirationDate = expirationDate;
    }
    public String getKey() {return key;}
    public void setKey(String key) {this.key = key;}
    public LocalDate setExpirationDate() {return expirationDate;}
    public void setExpirationDate(LocalDate expirationDate) {this.expirationDate = expirationDate;}

}
