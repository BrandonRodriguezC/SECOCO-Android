package com.example.secoco.usuarios.erc_covid.recyclerView;

import com.example.secoco.entities.UsuarioAPI;
import com.example.secoco.usuarios.erc_covid.ReporteNotificarCita;

public class Item {

    private int tipo, tipoMensaje;
    private UsuarioAPI usuario;
    private ReporteNotificarCita reporteNotificarCita;
    private String fecha;

    public Item(int tipo, UsuarioAPI usuario, String fecha) {
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

    public UsuarioAPI getUsuario() {
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
