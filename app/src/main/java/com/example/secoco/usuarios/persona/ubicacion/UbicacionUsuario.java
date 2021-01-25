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

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.secoco.Ingreso;
import com.example.secoco.R;
import com.example.secoco.general.RequestAPI;
import com.example.secoco.general.VariablesGenerales;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

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
        ubicacionUsuario = new HiloUbicacionUsuario(this.intervalo, rangoMaximo, usuario, this);

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
        Intent intent = new Intent(getApplicationContext(), Ingreso.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), id_canal);
        builder.setContentTitle("SeCoCo");
        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
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
        throw new UnsupportedOperationException("Sin Implementar");
    }

    static class HiloUbicacionUsuario extends Thread {

        private double latitud, longitud;
        private final double rangoMaximo;
        private final long intervalo;
        private final String usuario;
        private boolean estaVivo;
        private Context context;

        public HiloUbicacionUsuario(long intervalo, double rangoMaximo, String usuario, Context context) {
            this.intervalo = intervalo;
            this.rangoMaximo = rangoMaximo;
            this.usuario = usuario;
            this.latitud = 0;
            this.longitud = 0;
            this.estaVivo = true;
            this.context = context;
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
            String[] fechaHora = obtenerFecha(minutos);
            JSONObject request = new JSONObject();
            try {
                request.put("U", this.usuario);
                request.put("F", fechaHora[0]);
                request.put("HI", Integer.parseInt(fechaHora[1]));
                request.put("HF", Integer.parseInt(fechaHora[2]));
                request.put("Lat", latitudNueva);
                request.put("Lon", longitudNueva);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    "https://secocobackend.glitch.me/REPORTE-UBICACION",
                    request,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            System.out.println(response.toString());
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println("Error");
                        }
                    });
            jsonObjectRequest.setShouldCache(false);
            RequestAPI.getInstance(context).add(jsonObjectRequest);
        }

    }

}
