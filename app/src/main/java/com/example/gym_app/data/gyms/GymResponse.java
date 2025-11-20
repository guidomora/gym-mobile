package com.example.gym_app.data.gyms;

public class GymResponse {
    private Long id;
    private String nombre;

    public Long getId() { return id; }
    public String getNombre() { return nombre; }

    @Override
    public String toString() {
        return nombre;
    }
}