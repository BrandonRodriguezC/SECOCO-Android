package com.example.secoco.usuarios.persona;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.secoco.Ingreso;
import com.example.secoco.R;
import com.example.secoco.usuarios.persona.ubicacion.UbicacionUsuario;
import com.example.secoco.general.VariablesGenerales;

public class PersonaInicio extends AppCompatActivity implements View.OnClickListener {


    //Atributos
    private Button btnUbicación;
    private Button btnMapa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persona_inicio);

        //Inicialización de Atributos
        this.btnUbicación = (Button) findViewById(R.id.btn_ubicacion);
        this.btnMapa = (Button) findViewById(R.id.btn_mapa);

        //Inicia a tomar y validar las coordenadas (latitud y longitud)
        iniciarReporteUbicacion();

        //Acción de Botones
        this.btnUbicación.setOnClickListener(this);
        this.btnMapa.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == this.btnUbicación.getId()) {
            finalizarServicio();
        } else if (view.getId() == this.btnMapa.getId()) {
            Intent nuevaActividad = new Intent(PersonaInicio.this, Mapa.class);
            nuevaActividad.putExtra("USUARIO", getIntent().getStringExtra("USUARIO"));
            startActivity(nuevaActividad);
        }
    }

    private void iniciarReporteUbicacion() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    VariablesGenerales.CODIGO_REQUEST_EXITOSO);
        } else {
            verificarGPS(PersonaInicio.this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == VariablesGenerales.CODIGO_REQUEST_EXITOSO && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                verificarGPS(PersonaInicio.this);
            } else {
                Toast.makeText(PersonaInicio.this, "Permisos Denegados", Toast.LENGTH_SHORT);
            }
        }
    }

    public void verificarGPS(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            iniciarServicio();
        } else {
            //Arreglar para que cuando no este prendido lo solicite
            Toast.makeText(PersonaInicio.this, "GPS desactivado, favor activarlo he intentar de nuevo", Toast.LENGTH_LONG).show();
            Intent ingreso = new Intent(PersonaInicio.this, Ingreso.class);
            startActivity(ingreso);
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
            intent.setAction(VariablesGenerales.ACCION_INICIO);
            intent.putExtra("usuario", getIntent().getStringExtra("USUARIO"));
            startService(intent);
            Toast.makeText(PersonaInicio.this, "Servicio de Analisis de Ubicación Activado", Toast.LENGTH_LONG).show();
        }
    }

    private void finalizarServicio() {
        if (estaEjecutandoseReporteUbicacion()) {
            Intent intent = new Intent(getApplicationContext(), UbicacionUsuario.class);
            intent.setAction(VariablesGenerales.ACCION_FINAL);
            startService(intent);
            Toast.makeText(PersonaInicio.this, "Servicio de Analisis de Ubicación Terminado", Toast.LENGTH_LONG).show();
        }
    }

}