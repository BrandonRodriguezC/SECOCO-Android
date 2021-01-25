package com.example.secoco.usuarios.persona;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.secoco.Ingreso;
import com.example.secoco.R;
import com.example.secoco.entities.UbicacionAPI;
import com.example.secoco.general.RequestAPI;
import com.example.secoco.general.ServicioUbicacion;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

public class Mapa extends AppCompatActivity implements OnMapReadyCallback, MapboxMap.OnMapClickListener, PermissionsListener, NavigationView.OnNavigationItemSelectedListener {

    private MapView mapView;
    private MapboxMap mapboxMap;
    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;
    private Button btn1, btn2;
    private DirectionsRoute currentRoute;

    // MAPBOX --------------------
    private static final String SOURCE_ID = "SOURCE_ID";
    private static final String ICON_ID = "ICON_ID";
    private static final String LAYER_ID = "LAYER_ID";

    private List<Feature> symbolLayerIconFeatureList = new ArrayList<Feature>();
    private List<UbicacionAPI> ubicaciones = new ArrayList<UbicacionAPI>();

    private CardView tarjeta;

    //BARRA ----------------------
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;


    //POLIGONOS -------------------------
    private Style estilo;
    private Layer capa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_map);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        tarjeta = findViewById(R.id.tarjeta);
        btn1 = (Button) findViewById(R.id.btn_Historial_Desplazamientos);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                historicoDesplazamientos(getIntent().getStringExtra("USUARIO"));
            }
        });

        btn2 = (Button) findViewById(R.id.bnt_Manejo_aislamiento);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manejoAislamiento();
            }
        });

        //MENU LATERAL - RECORDAR IMPLEMENTS NavigationView.OnNavigationItemSelectedListener
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.bringToFront();
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }

    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/brandonrodriguez/ckk77d7ny04dn18o4cz1ccvcz")
                .withImage(ICON_ID, BitmapFactory.decodeResource(
                        Mapa.this.getResources(), R.drawable.mapbox_marker_icon_default))
                .withSource(new GeoJsonSource(SOURCE_ID,
                        FeatureCollection.fromFeatures(symbolLayerIconFeatureList)))
                .withLayer(new SymbolLayer(LAYER_ID, SOURCE_ID)
                        .withProperties(
                                iconImage(ICON_ID),
                                iconAllowOverlap(true),
                                iconIgnorePlacement(true)
                        )
                )

                , new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                Mapa.this.estilo = style;
                mapboxMap.addOnMapClickListener(Mapa.this);
                enableLocationComponent(mapboxMap.getStyle());
            }
        });
        this.mapboxMap.getUiSettings().setAttributionEnabled(false);
        this.mapboxMap.getUiSettings().setLogoEnabled(false);
    }

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
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
        if (tarjeta.getVisibility() != View.INVISIBLE) {
            tarjeta.setVisibility(View.INVISIBLE);
            capa.setProperties(PropertyFactory.fillOpacity((float)0));

        }
        return handleClickIcon(mapboxMap.getProjection().toScreenLocation(point));
    }


    private boolean handleClickIcon(PointF screenPoint) {
        List<Feature> features = mapboxMap.queryRenderedFeatures(screenPoint, LAYER_ID);
        if (!features.isEmpty()) {
            TextView fecha = findViewById(R.id.tarjeta_fecha);
            int i = features.get(0).getProperty("I").getAsInt();
            UbicacionAPI obj = ubicaciones.get(i);
            fecha.setText("Fecha: " + obj.F + "\nHora inicio: " + (int) obj.HI / 100 + ":" + (int) obj.HI % 100 + "\nHora final: " + (int) obj.HF / 100 + ":" + (int) obj.HF % 100);
            if (tarjeta.getVisibility() == View.INVISIBLE) {
                tarjeta.setVisibility(View.VISIBLE);
            }
            return true;
        } else {
            return false;
        }
    }

    private void historicoDesplazamientos(String usuario) {
        JSONObject request = new JSONObject();
        try {
            request.put("usuario", usuario);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                "https://secocobackend.glitch.me/HISTORIAL-DESPLAZAMIENTOS",
                request,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            GeoJsonSource marker = mapboxMap.getStyle().getSourceAs(SOURCE_ID);
                            JSONArray a = response.getJSONArray("datosUbicaciones");
                            Gson gson = new Gson();
                            for (int i = 0; i < a.length(); i++) {
                                String res = a.getJSONObject(i).toString();
                                UbicacionAPI obj = new UbicacionAPI(a.getJSONObject(i).getString("Lat"), a.getJSONObject(i).getString("Lon"), a.getJSONObject(i).getString("F"), a.getJSONObject(i).getString("HI"), a.getJSONObject(i).getString("HF"), a.getJSONObject(i).getString("Z"));
                                ubicaciones.add(obj);
                                Feature f = Feature.fromGeometry(Point.fromLngLat(obj.getLon(), obj.getLat()));
                                f.addNumberProperty("I", i);
                                symbolLayerIconFeatureList.add(f);
                            }
                            marker.setGeoJson(FeatureCollection.fromFeatures(symbolLayerIconFeatureList));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Error");
                    }
                });
        jsonObjectRequest.setShouldCache(false);
        RequestAPI.getInstance(this).add(jsonObjectRequest);
    }

    private void manejoAislamiento() {
        JSONObject request = new JSONObject();
        try {
            double latitud = locationComponent.getLastKnownLocation().getLatitude();
            double longitud = locationComponent.getLastKnownLocation().getLongitude();
            request.put("Latitud", latitud);
            request.put("Longitud", longitud);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                "https://secocobackend.glitch.me/MANEJO-AISLAMIENTO",
                request,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            double activos = response.getDouble("Activos");
                            double posibles = response.getDouble("Posibles");

                            String localidad = response.getString("Localidad");
                            capa = estilo.getLayer(localidad+" P");
                            capa.setProperties(PropertyFactory.fillOpacity((float)0.8));
                            if(activos+posibles > 0.6){
                                capa.setProperties(PropertyFactory.fillColor(Color.parseColor("#D1001C")));
                            }else if (activos+posibles > 0.3){
                                capa.setProperties(PropertyFactory.fillColor(Color.parseColor("#F9C70C")));
                            }else {
                                capa.setProperties(PropertyFactory.fillColor(Color.parseColor("#006A4E")));
                            }
                            TextView reporte = findViewById(R.id.tarjeta_fecha);
                            reporte.setText("En tu localidad el " + String.format("%.2f", activos * 100) + "% de las personas inscritas " +
                                    "a SeCoCo son activas de COVID-19, el " + String.format("%.2f", posibles * 100) + "% son sospechosas y el " +
                                    String.format("%.2f", (1 - (activos + posibles)) * 100) + "% son inactivas, según estos datos tú decides si " +
                                    "aplicar aislamiento preventivo");
                            //Layer localidad =
                            if (tarjeta.getVisibility() == View.INVISIBLE) {
                                tarjeta.setVisibility(View.VISIBLE);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Error");
                    }
                });
        jsonObjectRequest.setShouldCache(false);
        RequestAPI.getInstance(this).add(jsonObjectRequest);
    }

    //LISTENER MENU LATERAL ------------------------------
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.toString().equals("Cerrar sesion")) {
            ServicioUbicacion.finalizarServicio(this);
            Intent login = new Intent(Mapa.this, Ingreso.class);
            startActivity(login);
            finish();
        } else if (item.toString().equals("Perfil")) {
            Intent actividad = new Intent(Mapa.this, PersonaInicio.class);
            actividad.putExtra("USUARIO", getIntent().getStringExtra("USUARIO"));
            actividad.putExtra("NOMBRE", getIntent().getStringExtra("NOMBRE"));
            actividad.putExtra("ID", getIntent().getStringExtra("ID"));
            actividad.putExtra("FECHA_NACIMIENTO", getIntent().getStringExtra("FECHA_NACIMIENTO"));
            actividad.putExtra("CORREO", getIntent().getStringExtra("CORREO"));
            actividad.putExtra("LOCALIDAD", getIntent().getStringExtra("LOCALIDAD"));
            actividad.putExtra("DIRECCION", getIntent().getStringExtra("DIRECCION"));
            actividad.putExtra("SINTOMAS", getIntent().getStringExtra("SINTOMAS"));
            actividad.putExtra("RESULTADO", getIntent().getStringExtra("RESULTADO"));
            actividad.putExtra("ZONA", getIntent().getStringExtra("ZONA"));
            startActivity(actividad);
            finish();
        } else if (item.toString().equals("Desactivar ubicacion")) {
            ServicioUbicacion.finalizarServicio(this);
        } else if (item.toString().equals("Sintomas")) {
            Intent actividad = new Intent(Mapa.this, Sintomas.class);
            actividad.putExtra("USUARIO", getIntent().getStringExtra("USUARIO"));
            actividad.putExtra("NOMBRE", getIntent().getStringExtra("NOMBRE"));
            actividad.putExtra("ID", getIntent().getStringExtra("ID"));
            actividad.putExtra("FECHA_NACIMIENTO", getIntent().getStringExtra("FECHA_NACIMIENTO"));
            actividad.putExtra("CORREO", getIntent().getStringExtra("CORREO"));
            actividad.putExtra("LOCALIDAD", getIntent().getStringExtra("LOCALIDAD"));
            actividad.putExtra("DIRECCION", getIntent().getStringExtra("DIRECCION"));
            actividad.putExtra("SINTOMAS", getIntent().getStringExtra("SINTOMAS"));
            actividad.putExtra("RESULTADO", getIntent().getStringExtra("RESULTADO"));
            actividad.putExtra("ZONA", getIntent().getStringExtra("ZONA"));
            startActivity(actividad);
            finish();
        }
        return false;
    }

}

