package com.example.secoco.entities;

public class Zona {

    private double Lon, Lat;
    private String I;

    public Zona(double lonMa, double lonMi, double latMi, double latMa, String i, String n) {
        Lon = lonMa;
        Lat = latMi;
        I = i;
    }

    public double getLonMa() {
        return Lon;
    }

    public double getLatMi() {
        return Lat;
    }

    public String getI() {
        return I;
    }

    public void setLonMa(double lon) {
        Lon = lon;
    }

    public void setLatMi(double lat) {
        Lat = lat;
    }

    public void setI(String I) {
        this.I = I;
    }
}
