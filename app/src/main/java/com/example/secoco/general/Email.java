package com.example.secoco.general;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import com.example.secoco.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Email {

    /*Metodo que permite enviar correos en Background pero con dependencia de
     * del correo y contraseña del origen
     * String origen [] = {EmailOrigen, ConstraseñaOrigen}
     * String mensaje [] = {EmailDestino, Asunto, Mensaje, accion (Opcional),
     *                     rutaBaseDeDatos (Opcional), valor (Opcional)} */
    public static void enviarCorreoBackGround(View view, String origen[], String... mensaje) {
        JavaMail javaMailAPI = new JavaMail(((Activity) view.getContext()), origen);
        javaMailAPI.execute(mensaje);
    }

    /* Metodo que permite enviar correos mediante gestores de correos instalados en el celular */
    public static void enviarCorreoForeGround(View view, String[] correos, String asunto, String mensaje) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setData(Uri.parse("mailto"));
        intent.putExtra(Intent.EXTRA_EMAIL, correos);
        intent.putExtra(Intent.EXTRA_SUBJECT, asunto);
        intent.putExtra(Intent.EXTRA_TEXT, mensaje);
        intent.setType("message/rfc822");
        Intent opciones = intent.createChooser(intent, "¿Porque medio desea enviar el correo?");
        ((Activity) view.getContext()).startActivityForResult(opciones, 1);
    }

    /* Metodo que permite leer los archivos que se encuentran en la carpeta (res.raw) */
    public static String leerMensaje(InputStream archivo) {
        try {
            BufferedReader a = new BufferedReader(new InputStreamReader(archivo));
            String linea = "", mensaje = "";
            while ((linea = a.readLine()) != null) {
                mensaje += linea + "\n";
            }
            return mensaje;
        } catch (IOException e) {
            Log.e("Error Archivo", "Archivo no Encontrado");
        }
        return null;
    }


    /* Metodo encargado de cargar el mensaje personalizado y agregar los datos de cada usuario
     * informacionMensaje[0] = Nombre
     * informacionMensaje[1] = Documento Identidad
     * informacionMensaje[2] = fecha de citación
     * infromacionMensaje[3] = sintomas
     * informacionMensaje[4] = Dirección */
    public static String mensajePersonalizado(View view, String[] informacionMensaje) {
        String email = Email.leerMensaje(view.getResources().openRawResource(R.raw.email_personalizado));

        email = email.replace("NNN", informacionMensaje[0]);
        email = email.replace("DDD", informacionMensaje[1]);
        email = email.replace("FFF", informacionMensaje[2]);
        String sintomas = informacionMensaje[3];

        email = sintomas.charAt(0) != '0' ? email.replace("QQQ,", view.getContext().getText(R.string.reporteNotificacion_check_dif_respirar) + ",") :
                email.replace("QQQ,", "");
        email = sintomas.charAt(1) != '0' ? email.replace("WWW,", view.getContext().getText(R.string.reporteNotificacion_check_fiebre) + ",") :
                email.replace("WWW,", "");
        email = sintomas.charAt(2) != '0' ? email.replace("EEE,", view.getContext().getText(R.string.reporteNotificacion_check_fatiga) + ",") :
                email.replace("EEE,", "");
        email = sintomas.charAt(3) != '0' ? email.replace("RRR,", view.getContext().getText(R.string.reporteNotificacion_check_contacto) + ",") :
                email.replace("RRR,", "");
        email = sintomas.charAt(4) != '0' ? email.replace("TTT,", view.getContext().getText(R.string.reporteNotificacion_check_tos) + ",") :
                email.replace("TTT,", "");
        email = sintomas.charAt(5) != '0' ? email.replace("YYY,", view.getContext().getText(R.string.reporteNotificacion_check_dis_olfato_sabor) + ",") :
                email.replace("YYY", "");

        email = email.replace("UUU", informacionMensaje[4]);

        return email;
    }


    /* Metodo encargado de cargar el mensaje masivo y agregar los datos generalizados
     * fecha = fechaDeCitación
     * sintomas = SintomasDePaciente */
    public static String mensajeMasivo(View view, String fecha, String sintomas) {
        String email = Email.leerMensaje(view.getResources().openRawResource(R.raw.email_masivo));

        email = sintomas.charAt(0) != '0' ? email.replace("QQQ,", view.getContext().getText(R.string.reporteNotificacion_check_dif_respirar) + ",") :
                email.replace("QQQ,", "");
        email = sintomas.charAt(1) != '0' ? email.replace("WWW,", view.getContext().getText(R.string.reporteNotificacion_check_fiebre) + ",") :
                email.replace("WWW,", "");
        email = sintomas.charAt(2) != '0' ? email.replace("EEE,", view.getContext().getText(R.string.reporteNotificacion_check_fatiga) + ",") :
                email.replace("EEE,", "");
        email = sintomas.charAt(3) != '0' ? email.replace("RRR,", view.getContext().getText(R.string.reporteNotificacion_check_contacto) + ",") :
                email.replace("RRR,", "");
        email = sintomas.charAt(4) != '0' ? email.replace("TTT,", view.getContext().getText(R.string.reporteNotificacion_check_tos) + ",") :
                email.replace("TTT,", "");
        email = sintomas.charAt(5) != '0' ? email.replace("YYY;", view.getContext().getText(R.string.reporteNotificacion_check_dis_olfato_sabor) + ";") :
                email.replace("YYY;", ";");
        email = email.replace("FFF", fecha);

        return email;
    }


}


