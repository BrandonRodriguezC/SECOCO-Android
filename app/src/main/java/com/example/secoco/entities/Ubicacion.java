package com.example.secoco.entities;

public class Ubicacion {

    //Lat = Latitud, Long = Longitud, F = Fecha, HI = HoraInicio y HF = HoraFinal
    public double Lat, Lon;
    public String F;
    public int HI, HF;

    public Ubicacion(double latitud, double longitud, String fecha, String horaInicio, String horaFin) {
        this.Lat = latitud;
        this.Lon = longitud;
        this.F = fecha;
        this.HI = Integer.parseInt(horaInicio);
        this.HF = Integer.parseInt(horaFin);
    }


}
