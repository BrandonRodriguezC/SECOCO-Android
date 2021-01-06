package com.example.secoco.entities;

public class Zona {

    public String L, N, C;
    private double latitudMin, longitudMin, latitudMax, longitudMax;

    public Zona() {

    }

    public Zona(String coordenadas, String nombre, String cuarentena) {
        this.L = coordenadas;
        this.N = nombre;
        this.C = cuarentena;
        this.latitudMin = 0;
        this.longitudMin = 0;
        this.latitudMax = 0;
        this.longitudMax = 0;
    }

    public void generarCoordenadas() {
        String coor[] = this.L.split(" ");
        this.latitudMin = Double.parseDouble(coor[0]);
        this.longitudMin = Double.parseDouble(coor[1]);
        this.latitudMax = Double.parseDouble(coor[2]);
        this.longitudMax = Double.parseDouble(coor[3]);
    }

    public double getLatitudMin() {
        return latitudMin;
    }

    public double getLongitudMin() {
        return longitudMin;
    }

    public double getLatitudMax() {
        return latitudMax;
    }

    public double getLongitudMax() {
        return longitudMax;
    }
}
