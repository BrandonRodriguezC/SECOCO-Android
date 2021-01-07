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
import com.example.secoco.entities.Zona;
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
import java.util.ArrayList;
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

    class HiloUbicacionUsuario extends Thread {

        private double latitud, longitud;
        private double rangoMaximo;
        private long intervalo;
        private String usuario;
        private boolean estaVivo;
        private ArrayList<Zona> zonas;

        public HiloUbicacionUsuario(long intervalo, double rangoMaximo, String usuario) {
            this.intervalo = intervalo;
            this.rangoMaximo = rangoMaximo;
            this.usuario = usuario;
            this.latitud = 0;
            this.longitud = 0;
            this.estaVivo = true;
            this.zonas = new ArrayList<Zona>();
            //Carga en el dispositivo las zonas disponibles para poder identificar
            //en que zona se encuentra cada usuario
            cargarZonas();
        }

        @Override
        public void run() {
            double latitudNueva = 0, latitudVieja = 0, longitudNueva = 0, longitudVieja = 0;
            int minutos = 0;
            DatabaseReference baseDatos = FirebaseDatabase.getInstance().getReference("ubicaciones");

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
                        int zona = 0, i = 0;
                        boolean tieneZona = false;
                        while (i < zonas.size() && !tieneZona) {
                            double coordenadasZona[] = zonas.get(i).generarCoordenadas();
                            if (latitudNueva >= coordenadasZona[0] && latitudNueva <= coordenadasZona[2]
                                    && longitudNueva >= coordenadasZona[1] && longitudNueva <= coordenadasZona[3]) {
                                zona = i + 1;
                                tieneZona = true;
                            }
                            i++;
                        }
                        String fechaHora[] = obtenerFecha(minutos);
                        baseDatos.child(usuario).child(fechaHora[0]).child(fechaHora[1] + " " + fechaHora[2]).setValue(
                                new Ubicacion(latitudNueva, longitudNueva, usuario, zona));
                        latitudVieja = latitudNueva;
                        longitudVieja = longitudNueva;
                        minutos = 0;
                    }
                }
            }
        }

        private String[] obtenerFecha(int tiempo) {
            String fecha = "";
            Calendar fechaInicio = Calendar.getInstance();
            fechaInicio.setTime(new Date());
            DateFormat formatoFecha = new SimpleDateFormat("HHmm");
            fecha = formatoFecha.format(fechaInicio.getTime());
            fechaInicio.set(Calendar.MINUTE, fechaInicio.get(Calendar.MINUTE) - tiempo);
            formatoFecha = new SimpleDateFormat("dd-MM-yyyy HHmm");
            fecha = formatoFecha.format(fechaInicio.getTime()) + " " + fecha;
            return fecha.split(" ");
        }

        private void cargarZonas() {
            DatabaseReference baseDatos = FirebaseDatabase.getInstance().getReference("zonas");
            baseDatos.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot zona : snapshot.getChildren()) {
                        Zona z = zona.getValue(Zona.class);
                        zonas.add(z);
                    }
                    Log.i("Cargado de Zonas", "Correcto");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.i("Cargado de Zonas", "Incorrecto");
                }
            });
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

    }

}
