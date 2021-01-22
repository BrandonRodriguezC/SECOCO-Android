package com.example.secoco.entities;

public class UbicacionAPI {
    public double Lat, Lon;
    public String F;
    public int HI, HF, Z;

    public UbicacionAPI(String latitud, String longitud, String fecha, String horaInicio, String horaFin, String Z) {
        this.Lat = Double.parseDouble(latitud);
        this.Lon = Double.parseDouble(longitud);
        this.F = fecha;
        this.HI = Integer.parseInt(horaInicio);
        this.HF = Integer.parseInt(horaFin);
        this.Z = Integer.parseInt(Z);
    }

    public double getLat() {
        return Lat;
    }

    public void setLat(double lat) {
        Lat = lat;
    }

    public double getLon() {
        return Lon;
    }

    public void setLon(double lon) {
        Lon = lon;
    }

    public String getF() {
        return F;
    }

    public void setF(String f) {
        F = f;
    }

    public int getHI() {
        return HI;
    }

    public void setHI(int HI) {
        this.HI = HI;
    }

    public int getHF() {
        return HF;
    }

    public void setHF(int HF) {
        this.HF = HF;
    }

    public int getZ() {
        return Z;
    }

    public void setZ(int z) {
        Z = z;
    }
}

