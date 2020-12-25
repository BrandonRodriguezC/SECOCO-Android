package com.example.secoco.usuarios.persona;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import com.example.secoco.entities.Ubicacion;
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

public class ServicioUbicacion extends Service {

    private long intervalo;
    private UbicacionUsuario ubicacionUsuario;
    private LocationCallback locationCallback;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null){
            String accion = intent.getAction();
            if(accion != null){
                if(accion.equals(VariablesServicio.ACCION_INICIO)){
                    inicializarAtributos(intent);
                    inicioServicio();
                }else if(accion.equals(VariablesServicio.ACCION_FINAL)){
                    finalizarServicio();
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void inicializarAtributos(Intent intent){
        this.intervalo =  intent.getLongExtra("intervalo", 5)*60000;
        double rangoMaximo = intent.getDoubleExtra("rangoMaximo", 0.005);
        String usuario = intent.getStringExtra("usuario");
        ubicacionUsuario = new UbicacionUsuario(this.intervalo, rangoMaximo, usuario);

        this.locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult != null && locationResult.getLocations().size() > 0) {
                    ubicacionUsuario.setLatitud(locationResult.getLastLocation().getLatitude());
                    ubicacionUsuario.setLongitud(locationResult.getLastLocation().getLongitude());
                }
            }
        };
    }

    private void inicioServicio() {
        String id_canal = "id_notificación";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(), 0,  intent, PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder( getApplicationContext(), id_canal);
        builder.setContentTitle("SeCoCo");
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setContentText("Analizando su Ubicación");
        builder.setAutoCancel(false);
        builder.setContentIntent(pendingIntent);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);

        LocationRequest ubicacion = new LocationRequest();
        ubicacion.setInterval(this.intervalo-1000);
        ubicacion.setFastestInterval(this.intervalo-1000);
        ubicacion.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("Error de Permisos", "No se han aceptado los servicios");
        }else{
            LocationServices.getFusedLocationProviderClient(this).
                    requestLocationUpdates(ubicacion, locationCallback, Looper.getMainLooper());
            ubicacionUsuario.start();
            startForeground(VariablesServicio.ID_SERVICIO, builder.build());
        }
    }

    private void finalizarServicio(){
        LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);
        stopForeground(true);
        ubicacionUsuario.setEstaVivo(false);
        stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Todavia no ha sido implementado");
    }

    class UbicacionUsuario extends Thread {

        private double latitud, longitud;
        private double rangoMaximo;
        private long intervalo;
        private String usuario;
        private boolean estaVivo;

        public UbicacionUsuario(long intervalo, double rangoMaximo, String usuario) {
            this.intervalo = intervalo;
            this.rangoMaximo = rangoMaximo;
            this.usuario = usuario;
            this.latitud = 0;
            this.longitud = 0;
            this.estaVivo = true;
        }

        @Override
        public void run() {
            double latitudNueva = 0, latitudVieja = 0, longitudNueva = 0, longitudVieja = 0;
            int minutos = 0, segundos = 0;
            DatabaseReference baseDatos = FirebaseDatabase.getInstance().getReference();
            while (estaVivo) {
                latitudNueva = latitud;
                longitudNueva = longitud;
                if (minutos % this.intervalo / 60000 == 0 && segundos == 0) {
                    //Verificar Los minutos (tiempo) que se sube a la base de datos
                    //System.out.println("Dif Lat " + (Math.abs(latitudNueva - latitudVieja)) + " " + (Math.abs(latitudNueva - latitudVieja) > rangoMaximo));
                    //System.out.println("Dif Lon " + (Math.abs(longitudNueva - longitudVieja)) + " " + (Math.abs(longitudNueva - longitudVieja) > rangoMaximo));
                    //System.out.println("Entro " + minutos);
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
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (segundos == 60) {
                    minutos++;
                    segundos = 0;
                    //System.out.println("Minutos " + minutos);
                }
                segundos++;
            }
        }

        private String obtenerFecha(int tiempo) {
            Calendar calendario = Calendar.getInstance();
            calendario.setTime(new Date());
            calendario.set(Calendar.MINUTE, calendario.get(Calendar.MINUTE) - tiempo);
            DateFormat formatoFecha = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            return formatoFecha.format(calendario.getTime());
        }

        public void setLatitud(double latitud){
            this.latitud = latitud;
        }

        public void setLongitud(double longitud){
            this.longitud = longitud;
        }

        public void setEstaVivo(boolean estaVivo){
            this.estaVivo = estaVivo;
        }

    }

}
