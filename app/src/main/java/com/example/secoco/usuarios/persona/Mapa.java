package com.example.secoco.usuarios.persona;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.secoco.Ingreso;
import com.example.secoco.R;
import com.example.secoco.usuarios.persona.ubicacion.UbicacionUsuario;
import com.example.secoco.usuarios.persona.ubicacion.VariablesServicio;
import com.google.android.material.navigation.NavigationView;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import java.util.List;

// classes needed to initialize map
// classes needed to initialize map

public class Mapa extends AppCompatActivity implements OnMapReadyCallback, MapboxMap.OnMapClickListener, PermissionsListener, NavigationView.OnNavigationItemSelectedListener {
    // variables for adding location layer
    private MapView mapView;
    private MapboxMap mapboxMap;
    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;
    private Button button;
    private DirectionsRoute currentRoute;

    //barra
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_map);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        button = (Button) findViewById(R.id.startButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nuevaActividad = new Intent(Mapa.this, PersonaInicio.class);
                startActivity(nuevaActividad);
                finish();
            }
        });


        //MENU LATERAL - RECORDAR IMPLEMENTS NavigationView.OnNavigationItemSelectedListener
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.bringToFront();
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }
        //navigationView.bringToFront();
        navigationView.setCheckedItem(R.id.nav_mapa);

    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(getString(R.string.navigation_guidance_day), new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                mapboxMap.addOnMapClickListener(Mapa.this);
                enableLocationComponent(mapboxMap.getStyle());
            }
        });
    }

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
// Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
// Activate the MapboxMap LocationComponent to show user location

            //DIEGO -
            // Toca que revise como tomar la ubicacion ya propia del gps porque lo que hace este metodo es
            // que la toma de manera implicita (segun entiendo) y le aplica estilo en el mapa, logicamente
            // no se está agregando una latitud o longitud exacta, literalmente despues de aprobar el
            // permiso solo se ejecutan estas 4 lineas. xd

            locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(LocationComponentActivationOptions.builder(this, mapboxMap.getStyle()).build());
            locationComponent.setLocationComponentEnabled(true);
            locationComponent.setCameraMode(CameraMode.TRACKING);

        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationComponent(mapboxMap.getStyle());
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        return false;
    }


    //LISTENER MENU LATERAL
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //Toast.makeText(this, "Sintomas"+getIntent().getStringExtra("USUARIO"), Toast.LENGTH_SHORT).show();
        if (item.toString().equals("Sintomas")) {
            Intent sintomas = new Intent(this, Sintomas.class);
            sintomas.putExtra("USUARIO", getIntent().getStringExtra("USUARIO"));
            startActivity(sintomas);
        } else if (item.toString().equals("Cerrar sesion")) {
            finalizarServicio();
            Intent login = new Intent(Mapa.this, Ingreso.class);
            startActivity(login);
            finish();
        }
        return false;
    }

    //Metodos para finalizar el servicio si se encuentra activo
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

    private void finalizarServicio() {
        if (estaEjecutandoseReporteUbicacion()) {
            Intent intent = new Intent(getApplicationContext(), UbicacionUsuario.class);
            intent.setAction(VariablesServicio.ACCION_FINAL);
            startService(intent);
            Toast.makeText(Mapa.this, "Analisis de Ubicación Finalizado", Toast.LENGTH_LONG).show();
        }
    }


}