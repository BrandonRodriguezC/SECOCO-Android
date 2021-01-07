package com.example.secoco.entities;

public class Ubicacion {

    //Latitud y Longitud (Separada por Espacio) | Usuario (U)
    public String L, U;
    //Zona de la latitud y Longitud
    public int Z;

    public Ubicacion(double latitud, double longitud, String usuario, int zona) {
        this.L = latitud + " " + longitud;
        this.U = usuario;
        this.Z = zona;
    }

    public double[] generarCoordenadas() {
        String[] coor = this.L.split(" ");
        // latitud = [0] y longitud = [1]
        return new double[]{Double.parseDouble(coor[0]), Double.parseDouble(coor[1])};
    }

}
