package com.example.secoco.usuarios.erc_covid.recyclerView;

import com.example.secoco.entities.Usuario;
import com.example.secoco.usuarios.erc_covid.ReporteNotificarCita;

public class Item {

    private int tipo, tipoMensaje;
    private Usuario usuario;
    private ReporteNotificarCita reporteNotificarCita;
    private String usuarioKey, fecha;

    public Item(int tipo, Usuario usuario, String usuarioKey, String fecha) {
        this.tipo = tipo;
        this.usuario = usuario;
        this.usuarioKey = usuarioKey;
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

    public Usuario getUsuario() {
        return usuario;
    }

    public int getTipoMensaje() {
        return tipoMensaje;
    }

    public String getUsuarioKey() {
        return usuarioKey;
    }

    public String getFecha() {
        return fecha;
    }

    public ReporteNotificarCita getReporteNotificarCita() {
        return reporteNotificarCita;
    }
}
