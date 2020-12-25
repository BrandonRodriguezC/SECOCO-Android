package com.example.secoco.usuarios.persona;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.secoco.entities.Ubicacion;
import com.example.secoco.R;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

public class PersonaInicio extends AppCompatActivity implements View.OnClickListener {

    private static int CODIGO_REQUEST_EXITOSO = 1;
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
            startActivity(nuevaActividad);
        }
    }

    private void iniciarReporteUbicacion() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    CODIGO_REQUEST_EXITOSO);
        }else{
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

    private boolean estaEjecutandoseReporteUbicacion(){
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null){
            for (ActivityManager.RunningServiceInfo servicio:
                    activityManager.getRunningServices(Integer.MAX_VALUE)){
                if(ServicioUbicacion.class.getName().equals(servicio.service.getClassName())){
                    if(servicio.foreground){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void iniciarServicio(){
        if(!estaEjecutandoseReporteUbicacion()){
            Intent intent = new Intent(getApplicationContext(), ServicioUbicacion.class);
            intent.setAction(VariablesServicio.ACCION_INICIO);
            intent.putExtra("intervalo", 1L);
            intent.putExtra("rangoMaximo", 0.00009);
            intent.putExtra("usuario", getIntent().getStringExtra("USUARIO"));
            startService(intent);
        }
    }

    private void finalizarServicio(){
        if(estaEjecutandoseReporteUbicacion()){
            Intent intent = new Intent(getApplicationContext(), ServicioUbicacion.class);
            intent.setAction(VariablesServicio.ACCION_FINAL);
            startService(intent);
        }
    }

}