package com.example.secoco.general;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.secoco.Ingreso;
import com.example.secoco.usuarios.persona.ubicacion.UbicacionUsuario;

public class ServicioUbicacion {

    public static void verificarGPS(AppCompatActivity activity) {
        PackageManager packageManager = activity.getPackageManager();
        if(packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)) {
            LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                iniciarServicio(activity);
            } else {
                mensajeGPS(activity, "Error de GPS",
                        "El GPS no se encuentra Activo, favor activarlo y volver a ingresar" ).show();
            }
        }else{
            mensajeGPS(activity, "GPS no Encontrado",
                    "Lamentablemente el dispositivo no cuenta con GPS, por lo cual se pierde la " +
                    "funcionalidad del aplicativo").show();
        }
    }

    private static boolean estaEjecutandoseReporteUbicacion(AppCompatActivity activity) {
        ActivityManager activityManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            for (ActivityManager.RunningServiceInfo servicio :
                    activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (UbicacionUsuario.class.getName().equals(servicio.service.getClassName())) {
                    if (servicio.foreground) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static void iniciarServicio(AppCompatActivity activity) {
        if (!estaEjecutandoseReporteUbicacion(activity)) {
            Intent intent = new Intent(activity, UbicacionUsuario.class);
            intent.setAction(VariablesGenerales.ACCION_INICIO);
            intent.putExtra("usuario", activity.getIntent().getStringExtra("USUARIO"));
            activity.startService(intent);
            Toast.makeText(activity, "Servicio de Analisis de Ubicación Activado", Toast.LENGTH_LONG).show();
        }
    }

    public static void finalizarServicio(AppCompatActivity activity) {
        if (estaEjecutandoseReporteUbicacion(activity)) {
            Intent intent = new Intent(activity, UbicacionUsuario.class);
            intent.setAction(VariablesGenerales.ACCION_FINAL);
            activity.startService(intent);
            Toast.makeText(activity, "Servicio de Analisis de Ubicación Terminado", Toast.LENGTH_LONG).show();
        }
    }

    private static AlertDialog.Builder mensajeGPS(AppCompatActivity activity, String titulo, String mensaje){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", (dialog, which) -> {
                    Intent ingreso = new Intent(activity, Ingreso.class);
                    activity.startActivity(ingreso);
                    activity.finish();
                });
        return builder;
    }

}
