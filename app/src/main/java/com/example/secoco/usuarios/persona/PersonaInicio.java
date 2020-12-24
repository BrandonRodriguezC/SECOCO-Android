package com.example.secoco.usuarios.persona;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
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
    private static String NOMBRE_HILO = "Hilo-Ubicacion";
    //Atributos
    private Button btnUbicación;
    private Button btnMapa;
    private UbicacionUsuario ubicacion;

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
            crearHiloReporteUbicacion();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODIGO_REQUEST_EXITOSO && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                crearHiloReporteUbicacion();
            } else {
                Toast.makeText(PersonaInicio.this, "Permisos Denegados", Toast.LENGTH_SHORT);
            }
        }
    }

    private void crearHiloReporteUbicacion(){
        boolean hiloExistente = false;
        Set<Thread> threads = Thread.getAllStackTraces().keySet();
        for (Thread t : threads) {
            if(t.getName() == this.NOMBRE_HILO){
                hiloExistente = true;
                break;
            }
        }
        if (!hiloExistente) {
            //Minutos y Rango minimo para insertar en la base de datos
            ubicacion = new UbicacionUsuario(1, 0.000005, getIntent().getStringExtra("USUARIO"));
            ubicacion.start();
            ubicacion.setName(this.NOMBRE_HILO);
        }else {
            System.out.println("Hilo no se puede crear, pues ya existe");
        }
    }

    class UbicacionUsuario extends Thread{

        private double latitud, longitud;
        private LocationRequest ubicacion;
        /* Cada cuantos minutos se envia una petición de localización y se envia a la base de datos */
        private long intervalo;
        /* Diferencia maxima que se puede tener entre la nueva y antigua latitud y longitud para guardarlo
        * en la base de datos */
        private double rangoMaximo;
        /* Usuario al cual se le hará seguimiento de la ubicación */
        private String usuario;

        public UbicacionUsuario (long intervalo, double rangoMaximo, String usuario){
            this.intervalo = 60000*intervalo;
            this.rangoMaximo = rangoMaximo;
            this.usuario = usuario;
        }

        @Override
        public void run(){
            ubicacion = new LocationRequest();
            ubicacion.setInterval(this.intervalo-10000);
            ubicacion.setFastestInterval(this.intervalo-10000);
            ubicacion();

            double latitudNueva = 0, latitudVieja = 0, longitudNueva = 0, longitudVieja = 0;
            int minutos=0, segundos=0;
            //System.out.println("Tiempo de Intervalos " + this.intervalo/60000);
            DatabaseReference baseDatos = FirebaseDatabase.getInstance().getReference();
            while(true){
                latitudNueva = latitud;
                longitudNueva = longitud;
                if (minutos % this.intervalo/60000 == 0 && segundos == 0) {
                    //System.out.println("Dif Lat " + Math.abs(latitudNueva - latitudVieja));
                    //System.out.println("Dif Lon " + Math.abs(longitudNueva - longitudVieja));
                    if (Math.abs(latitudNueva - latitudVieja) > rangoMaximo || Math.abs(longitudNueva - longitudVieja) > rangoMaximo) {
                        int tiempo = (int) (minutos * this.intervalo / 60000);
                        //System.out.println("Tiempo De Resta " + tiempo);
                        baseDatos.child("ubicaciones").child(usuario).child(obtenerFecha(tiempo)).setValue(
                                new Ubicacion(latitudNueva, longitudNueva, tiempo, 0));
                        latitudVieja = latitudNueva;
                        longitudVieja = longitudNueva;
                        minutos = 0;
                        segundos = 0;
                    }
                }
                //System.out.println("Latitud " + latitud + " Longitud " + longitud);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                segundos++;
                if (segundos == 60){
                    minutos++;
                    segundos = 0;
                    //System.out.println("Minutos " + minutos + " Segundos " + segundos);
                }
            }
        }

        public void ubicacion() {
            LocationRequest ubicacion = new LocationRequest();
            ubicacion.setInterval(this.intervalo-10000);
            ubicacion.setFastestInterval(this.intervalo-25000);
            ubicacion.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            if (ActivityCompat.checkSelfPermission(PersonaInicio.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(PersonaInicio.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            }else {
                LocationServices.getFusedLocationProviderClient(PersonaInicio.this).requestLocationUpdates(ubicacion, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            UbicacionUsuario.this.latitud = locationResult.getLastLocation().getLatitude();
                            UbicacionUsuario.this.longitud = locationResult.getLastLocation().getLongitude();
                            //System.out.println("Cantidad de Localizaciones: " + locationResult.getLocations().size());
                        }
                    }
                }, Looper.getMainLooper());
            }
        }

        /* obtenerFecha se encarga de tomar la fecha actual y restarle t tiempo para poder se insertado en la
        * base de datos teniendo en cuenta la fecha inicial real*/
        private String obtenerFecha(int tiempo){
            Calendar calendario = Calendar.getInstance();
            calendario.setTime(new Date());
            calendario.set(Calendar.MINUTE, calendario.get(Calendar.MINUTE)- tiempo);
            DateFormat formatoFecha = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            return formatoFecha.format(calendario.getTime());
        }
    }
}