package com.example.secoco.usuarios.etda_covid.recyclerViewAis;

import org.json.JSONObject;

public class ItemAis {
    private JSONObject localidad;
    //Contructor
    public ItemAis(JSONObject localidad) {
        this.localidad = localidad;

    }

    public JSONObject getLocalidad() {
        return localidad;
    }


}
