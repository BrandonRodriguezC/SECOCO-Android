package com.example.secoco.entities;

public class Ubicacion {

    //Latitud y Longitud (Separada por Espacio)
    public String L;
    //Tiempo en el que duro en latitudes y longitudes similares
    public int T;
    //Zona de la latitud y Longitud
    public int Z;

    public Ubicacion(double latitud, double longitud, int tiempo, int zona) {
        this.L = latitud + " " + longitud;
        this.T = tiempo;
        this.Z = zona;
    }

}
