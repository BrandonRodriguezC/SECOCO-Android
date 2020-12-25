package com.example.secoco.entities;

public class Usuario {

    public String N, M, I, C, D, E, X, F;

    public Usuario(String nombre, String apellido, String correo, String id, String contraseña,
                   String direccion, String tipo_id, String localidad, String estado, String fechaNacimiento) {
        this.N = nombre+ " "+ apellido;
        this.C = contraseña;
        this.M = correo;
        this.I = tipo_id + " "+ id;
        this.D =direccion+ ":" +localidad;
        this.E = "0000";
        if(estado.equals("Activo")){
            this.X = "- A";
        }else{
            this.X = "- I";
        }
        this.F= fechaNacimiento;
    }

}

