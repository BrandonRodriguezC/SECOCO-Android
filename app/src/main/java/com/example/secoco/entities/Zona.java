package com.example.secoco.entities;

public class Zona {

    public String L, N, C;

    public Zona() {

    }

    public Zona(String coordenadas, String nombre, String cuarentena) {
        this.L = coordenadas;
        this.N = nombre;
        this.C = cuarentena;
    }

    public double[] generarCoordenadas() {
        String[] coor = this.L.split(" ");
        //Retorna latitudMin = [0], longitudMin = [1], latitudMax = [2] y longitudMax = [3]
        return new double[]{Double.parseDouble(coor[0]), Double.parseDouble(coor[1]), Double.parseDouble(coor[2]), Double.parseDouble(coor[3])};
    }

}
