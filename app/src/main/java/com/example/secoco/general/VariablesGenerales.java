package com.example.secoco.general;

public class VariablesGenerales {

    /*--------Variables Necesarias para el funcionamiento de Servicio de Ubicación por GPS--------*/
    public static final int ID_SERVICIO = 100;
    public static final String ACCION_INICIO = "Iniciando Servicio";
    public static final String ACCION_FINAL = "Finalizando Servicio";
    public static final int CODIGO_REQUEST_EXITOSO = 1;

    /*------------------------Variables generales de agregado de Ubicación------------------------*/
    //Cada cuanto tiempo se agrega ubicaciones en la base de datos **Su estructura es 1 * 60000 (1 minuto)**
    public static final int INTERVALO_ENVIO_GPS = 15 * 60000;
    //Metros de distancia de rango maximo para poder insertar
    //Su estructura esta dada por una regla de 3 en donde 0.00001 en latitud o longitud equivale a 1.11 metros
    public static final double RANGO_MAXIMO_GPS = 5 * 0.00001 / 1.11;

    /*---------------------Variables generales de Analisis de Contaminados------------------------*/
    public static final double DISTANCIA_MAXIMA_ENTRE_PERSONAS = 5 * 0.00001 / 1.11;


    /*---------------------------Credenciales Envio Correos (Provicional)-------------------------*/
    public static String [] EMAIL_ORIGEN = {"pedroppax@gmail.com", "Pruebasecoco"};

}
