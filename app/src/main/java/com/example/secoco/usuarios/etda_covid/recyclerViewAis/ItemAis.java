package com.example.secoco.usuarios.etda_covid.recyclerViewAis;
import com.example.secoco.usuarios.etda_covid.Aislamiento;

import org.json.JSONObject;

public class ItemAis {
    private String localidad;
    private Aislamiento aislamiento;

    private JSONObject usuario;
    //Contructor
    public ItemAis(String localidad,Aislamiento aislamiento) {
        this.localidad=localidad;
        this.aislamiento = aislamiento;
    }
    public String getLocalidad() {
        return localidad;
    }
    public JSONObject getUsuario() {
        return usuario;
    }
    public Aislamiento getAislamiento() { return aislamiento; }
}
