package com.example.secoco.usuarios.erc_covid.recyclerView;

import com.example.secoco.usuarios.erc_covid.ReporteNotificarCita;

import org.json.JSONObject;

public class Item {

    private int tipo, tipoMensaje;
    private JSONObject usuario;
    private ReporteNotificarCita reporteNotificarCita;
    private String fecha;

    public Item(int tipo, JSONObject usuario, String fecha) {
        this.tipo = tipo;
        this.usuario = usuario;
        this.fecha = fecha;
    }

    public Item(int tipo, int mensaje, ReporteNotificarCita reporteNotificarCita) {
        this.tipo = tipo;
        this.tipoMensaje = mensaje;
        this.reporteNotificarCita = reporteNotificarCita;
    }

    public int getTipo() {
        return tipo;
    }

    public JSONObject getUsuario() {
        return usuario;
    }

    public int getTipoMensaje() {
        return tipoMensaje;
    }

    public String getFecha() {
        return fecha;
    }

    public ReporteNotificarCita getReporteNotificarCita() {
        return reporteNotificarCita;
    }
}
