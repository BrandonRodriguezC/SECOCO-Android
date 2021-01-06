package com.example.secoco.general;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.secoco.usuarios.persona.PersonaInicio;
import com.example.secoco.usuarios.persona.ubicacion.UbicacionUsuario;
import com.example.secoco.usuarios.persona.ubicacion.VariablesServicio;

public class ServicioUbicacion {

    /*Clase para generalizar la ejecución del Servicio de Ubicación
     * y poder iniciar y finalizarlo mediante la llamada de ServicioUbicacion.iniciarReporteUbicacion */

    /*private static int CODIGO_REQUEST_EXITOSO = 1;

    private void iniciarReporteUbicacion() {
        if (ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    CODIGO_REQUEST_EXITOSO);
        } else {
            iniciarServicio();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODIGO_REQUEST_EXITOSO && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                iniciarServicio();
            } else {
                Toast.makeText(PersonaInicio.this, "Permisos Denegados", Toast.LENGTH_SHORT);
            }
        }
    }

    private boolean estaEjecutandoseReporteUbicacion() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
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

    private void iniciarServicio() {
        if (!estaEjecutandoseReporteUbicacion()) {
            Intent intent = new Intent(getApplicationContext(), UbicacionUsuario.class);
            intent.setAction(VariablesServicio.ACCION_INICIO);
            //Cada cuanto tiempo se actualiza en la base de datos
            intent.putExtra("intervalo", 5);
            //Metros de distancia de rango maximo para poder insertar
            intent.putExtra("rangoMaximo", 5);
            intent.putExtra("usuario", getIntent().getStringExtra("USUARIO"));
            startService(intent);
            Toast.makeText(PersonaInicio.this, "Servicio de Analisis de Ubicación Activado", Toast.LENGTH_LONG).show();
        }
    }

    private void finalizarServicio() {
        if (estaEjecutandoseReporteUbicacion()) {
            Intent intent = new Intent(getApplicationContext(), UbicacionUsuario.class);
            intent.setAction(VariablesServicio.ACCION_FINAL);
            startService(intent);
            Toast.makeText(PersonaInicio.this, "Servicio de Analisis de Ubicación Terminado", Toast.LENGTH_LONG).show();
        }
    }*/

}
