package com.example.secoco.usuarios.persona.ubicacion;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.secoco.entities.Ubicacion;
import com.example.secoco.general.VariablesGenerales;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UbicacionUsuario extends Service {

    private long intervalo;
    private HiloUbicacionUsuario ubicacionUsuario;
    private LocationCallback locationCallback;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String accion = intent.getAction();
            if (accion != null) {
                if (accion.equals(VariablesGenerales.ACCION_INICIO)) {
                    inicializarAtributos(intent);
                    inicioServicio();
                } else if (accion.equals(VariablesGenerales.ACCION_FINAL)) {
                    finalizarServicio();
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void inicializarAtributos(Intent intent) {
        this.intervalo = VariablesGenerales.INTERVALO_ENVIO_GPS;
        double rangoMaximo = VariablesGenerales.RANGO_MAXIMO_GPS;

        String usuario = intent.getStringExtra("usuario");
        ubicacionUsuario = new HiloUbicacionUsuario(this.intervalo, rangoMaximo, usuario);

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
                getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), id_canal);
        builder.setContentTitle("SeCoCo");
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setContentText("Analizando su Ubicación");
        builder.setAutoCancel(false);
        builder.setContentIntent(pendingIntent);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            if (notificationManager != null && notificationManager.getNotificationChannel(id_canal) == null) {
                NotificationChannel notificationChannel = new NotificationChannel(id_canal,
                        "Analizando su Ubicación",
                        NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setDescription("Analisis de Ubicación - SeCoCo");
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        LocationRequest ubicacion = new LocationRequest();
        ubicacion.setInterval(this.intervalo + 2000);
        ubicacion.setFastestInterval(this.intervalo);
        ubicacion.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("Error de Permisos", "No se han aceptado los servicios");
        } else {
            LocationServices.getFusedLocationProviderClient(this).
                    requestLocationUpdates(ubicacion, locationCallback, Looper.getMainLooper());
            ubicacionUsuario.start();
            startForeground(VariablesGenerales.ID_SERVICIO, builder.build());
        }
    }

    private void finalizarServicio() {
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

    static class HiloUbicacionUsuario extends Thread {

        private double latitud, longitud;
        private final double rangoMaximo;
        private final long intervalo;
        private final String usuario;
        private boolean estaVivo;
        private int ultimoIndiceUbicacion;

        public HiloUbicacionUsuario(long intervalo, double rangoMaximo, String usuario) {
            this.intervalo = intervalo;
            this.rangoMaximo = rangoMaximo;
            this.usuario = usuario;
            this.latitud = 0;
            this.longitud = 0;
            this.estaVivo = true;
        }

        @Override
        public void run() {
            double latitudNueva, latitudVieja = 0, longitudNueva, longitudVieja = 0;
            int minutos = 0;

            while (estaVivo) {
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                minutos++;

                if (minutos % (this.intervalo / 60000) == 0) {
                    latitudNueva = latitud;
                    longitudNueva = longitud;
                    //System.out.println("Dif Lat " + (Math.abs(latitudNueva - latitudVieja)) + " " + (Math.abs(latitudNueva - latitudVieja) > rangoMaximo));
                    //System.out.println("Dif Lon " + (Math.abs(longitudNueva - longitudVieja)) + " " + (Math.abs(longitudNueva - longitudVieja) > rangoMaximo));
                    //System.out.println("Entro " + minutos);
                    if (Math.abs(latitudNueva - latitudVieja) > rangoMaximo || Math.abs(longitudNueva - longitudVieja) > rangoMaximo) {
                        //System.out.println("Tiempo De Resta " + tiempo);
                        ingresarUbicacion(latitudNueva, longitudNueva, minutos);
                        latitudVieja = latitudNueva;
                        longitudVieja = longitudNueva;
                        minutos = 0;
                    }
                }
            }
        }

        private String[] obtenerFecha(int tiempo) {
            String fecha;
            Calendar fechaInicio = Calendar.getInstance();
            fechaInicio.setTime(new Date());
            DateFormat formatoFecha = new SimpleDateFormat("HHmm");
            fecha = formatoFecha.format(fechaInicio.getTime());
            fechaInicio.set(Calendar.MINUTE, fechaInicio.get(Calendar.MINUTE) - tiempo);
            formatoFecha = new SimpleDateFormat("dd-MM-yyyy HHmm");
            fecha = formatoFecha.format(fechaInicio.getTime()) + " " + fecha;
            return fecha.split(" ");
        }

        public void setLatitud(double latitud) {
            this.latitud = latitud;
        }

        public void setLongitud(double longitud) {
            this.longitud = longitud;
        }

        public void setEstaVivo(boolean estaVivo) {
            this.estaVivo = estaVivo;
        }

        private void ingresarUbicacion(double latitudNueva, double longitudNueva, int minutos) {
            //DatabaseReference query se encarga de traer el ultimo elemento de la ubicación y poder
            //saber cual fue la ultima llave
            DatabaseReference query = FirebaseDatabase.getInstance().getReference("ubicaciones");
            query.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ultimoIndiceUbicacion = -10;
                    for (DataSnapshot indiceUbicacion : snapshot.getChildren()) {
                        ultimoIndiceUbicacion = Integer.parseInt(indiceUbicacion.getKey());
                    }
                    ultimoIndiceUbicacion = ultimoIndiceUbicacion != -10 ? ultimoIndiceUbicacion + 1 : 0;

                    String[] fechaHora = obtenerFecha(minutos);
                    // DatabaseReference agregaUbicacion se encarga de subir la ubicación a la base de datos
                    DatabaseReference agregarUbicacion = FirebaseDatabase.getInstance().getReference("ubicaciones");
                    agregarUbicacion.child(ultimoIndiceUbicacion + "").setValue(
                            new Ubicacion(latitudNueva, longitudNueva, fechaHora[0], fechaHora[1], fechaHora[2]));
                    //DatabaseReference actualizarUsuario se encarga de traer el ultimo elemento del usuario en
                    //relaciones y saber su indice
                    DatabaseReference indiceRelaciones = FirebaseDatabase.getInstance().getReference("relaciones/" + usuario);
                    indiceRelaciones.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int ultimoIndiceRelaciones = -10;
                            for (DataSnapshot ubicacion : snapshot.getChildren()) {
                                ultimoIndiceRelaciones = Integer.parseInt(ubicacion.getKey());
                            }
                            ultimoIndiceRelaciones = ultimoIndiceRelaciones != -10 ? ultimoIndiceRelaciones + 1 : 0;
                            //DatabaseReference agregarRelacion se encarga de enviar a relaciones el indice
                            //asociado del usuario a ubicaciones
                            DatabaseReference agregarRelacion = FirebaseDatabase.getInstance().getReference("relaciones/" + usuario);
                            agregarRelacion.child(ultimoIndiceRelaciones + "").setValue(ultimoIndiceUbicacion);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            System.out.println("Error al tomar el ultimo indice de relaciones y agregarlo");
                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    System.out.println("Error al tomar el ultimo indice de ubicaciones y agregarla");
                }
            });
        }

    }


}
