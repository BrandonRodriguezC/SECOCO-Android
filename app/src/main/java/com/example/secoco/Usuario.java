package com.example.secoco;

import java.util.HashMap;
import java.util.Map;

public class Usuario {

    public String nombre,apellido,correo , id , contraseña , direccion , tipo_id , localidad, estado;
    //Map<String, Estado> estadoMap;
    //Map<String, Examen> examenMap;
public Usuario(String nombre,String apellido,String correo ,String id , String contraseña ,
                String direccion , String tipo_id , String localidad, String estado ){
    this.nombre=nombre;
    this.apellido=apellido;
    this.correo =correo;
    this.id =id;
    this.contraseña=contraseña;
    this.direccion =direccion;
    this.tipo_id =tipo_id;
    this.localidad=localidad;

    /*estadoMap = new HashMap<>();
    estadoMap.put("Estado",new Estado());
    examenMap = new HashMap<>();
    if (estado.equals("Activo")){
        examenMap.put("Estado",new Examen(true));
    }else
    {
        examenMap.put("Estado",new Examen(false));
    }*/
    this.estado=estado;
}
}

