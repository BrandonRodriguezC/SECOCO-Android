package com.example.secoco.usuarios.persona;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.example.secoco.general.ServicioUbicacion;
import com.example.secoco.general.VariablesGenerales;

public class PersonaInicio extends AppCompatActivity implements View.OnClickListener {

    //Atributos
    private Button btnUbicacion;
    private Button btnMapa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persona_inicio);

        //Inicialización de Atributos
        this.btnUbicacion = (Button) findViewById(R.id.btn_ubicacion);
        this.btnMapa = (Button) findViewById(R.id.btn_mapa);

        //Inicia a tomar y validar las coordenadas (latitud y longitud)
        iniciarReporteUbicacion();

        //Acción de Botones
        this.btnUbicacion.setOnClickListener(this);
        this.btnMapa.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == this.btnUbicacion.getId()) {
            /*Metodo necesario para finalizar el reporte ubicación*/
            ServicioUbicacion.finalizarServicio(this);
        } else if (view.getId() == this.btnMapa.getId()) {
            Intent nuevaActividad = new Intent(PersonaInicio.this, Mapa.class);
            nuevaActividad.putExtra("USUARIO", getIntent().getStringExtra("USUARIO"));
            startActivity(nuevaActividad);
        }
    }

    /*-----------------------Metodos Necesarios para iniciar el reporte ubicación-----------------*/
    private void iniciarReporteUbicacion() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    VariablesGenerales.CODIGO_REQUEST_EXITOSO);
        } else {
            ServicioUbicacion.verificarGPS(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == VariablesGenerales.CODIGO_REQUEST_EXITOSO && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ServicioUbicacion.verificarGPS(this);
            } else {
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            VariablesGenerales.CODIGO_REQUEST_EXITOSO);
                }else{
                    Toast.makeText(PersonaInicio.this, "Permisos de Acceso a la Ubicación Denegados",
                            Toast.LENGTH_SHORT).show();
                    Intent ingreso = new Intent(PersonaInicio.this, Ingreso.class);
                    startActivity(ingreso);
                    finish();
                }
            }
        }
    }
    /*--------------------------------------------------------------------------------------------*/


}