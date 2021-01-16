package com.example.secoco.usuarios.ere_covid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.Bundle;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.secoco.R;
import com.example.secoco.general.RequestAPI;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

public class ReporteZona extends AppCompatActivity implements
        OnMapReadyCallback, MapboxMap.OnMapClickListener {

    private static final String SOURCE_ID = "SOURCE_ID";
    private static final String RED_ICON_ID = "RED_ICON_ID";
    private static final String LAYER_ID = "LAYER_ID";
    private MapView mapView;
    private MapboxMap mapboxMap;
    private List<Feature> puntosLocalidades;
    private TextView txtTitulo, txtActivos, txtInactivos, txtSolicitado, txtPendientes, txtExamenNoTomado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        Mapbox.getInstance(ReporteZona.this, getString(R.string.access_token));

        setContentView(R.layout.activity_reporte_zona);

        //Inicialización de Atributos
        this.puntosLocalidades = new ArrayList<>();
        this.txtTitulo = (TextView) findViewById(R.id.reporte_zona_txt_titulo);
        this.txtActivos = (TextView) findViewById(R.id.reporte_zona_txt_activos);
        this.txtInactivos = (TextView) findViewById(R.id.reporte_zona_txt_inactivos);
        this.txtSolicitado = (TextView) findViewById(R.id.reporte_zona_txt_solicitados);
        this.txtPendientes = (TextView) findViewById(R.id.reporte_zona_txt_pendientes);
        this.txtExamenNoTomado = (TextView) findViewById(R.id.reporte_zona_txt_examenNoTomado);


        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(getString(R.string.navigation_guidance_day), new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                mapboxMap.addOnMapClickListener(ReporteZona.this);
                style.addImage(RED_ICON_ID, BitmapFactory.decodeResource(
                        ReporteZona.this.getResources(), R.drawable.mapbox_marker_icon_default));
                style.addSource(new GeoJsonSource(SOURCE_ID));
                style.addLayer(new SymbolLayer(LAYER_ID, SOURCE_ID).withProperties(
                        iconImage(RED_ICON_ID),
                        iconAllowOverlap(true)
                ));
                //Realiza un request al servidor y trae las coordenadas de las localidades
                cargarZonas();
            }
        });
    }

    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        return accionIcono(mapboxMap.getProjection().toScreenLocation(point));
    }


    private void cargarZonas() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                "https://secocobackend.glitch.me/ZONAS",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray coorZonas = response.getJSONArray("coorZonas");
                            for (int i = 0; i < coorZonas.length(); i++) {
                                JSONObject zona = coorZonas.getJSONObject(i);
                                Feature puntoLocalidad = Feature.fromGeometry(
                                        Point.fromLngLat(zona.getDouble("Lon"),zona.getDouble("Lat")));
                                puntoLocalidad.addStringProperty("I", zona.getString("I"));
                                puntoLocalidad.addStringProperty("N", zona.getString("N"));
                                puntosLocalidades.add(puntoLocalidad);
                            }
                            GeoJsonSource geoJsonSource = mapboxMap.getStyle().getSourceAs(SOURCE_ID);
                            geoJsonSource.setGeoJson(FeatureCollection.fromFeatures(puntosLocalidades));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            System.out.println("Error de Transformación");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Error de Conectividad");
                    }
                });

        jsonObjectRequest.setShouldCache(true);
        RequestAPI.getInstance(this).add(jsonObjectRequest);
    }

    private boolean accionIcono(PointF punto){
        List<Feature> puntos = mapboxMap.queryRenderedFeatures(punto, LAYER_ID);
        if (!puntos.isEmpty()) {
            System.out.println(puntos.get(0).getProperty("I").getAsString() + " "+
                    puntos.get(0).getProperty("N").getAsString());
            JSONObject request = new JSONObject();
            try {
                request.put("Z", puntos.get(0).getProperty("I").getAsString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    "https://secocobackend.glitch.me/REPORTE-ZONA",
                    request,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            System.out.println(response.toString());
                            try {
                                txtTitulo.setText(puntos.get(0).getProperty("N").getAsString());
                                txtActivos.setText(response.getInt("A") + "");
                                txtInactivos.setText(response.getInt("I")+ "");
                                txtSolicitado.setText(response.getInt("S")+ "");
                                txtPendientes.setText(response.getInt("P")+ "");
                                txtExamenNoTomado.setText(response.getInt("N")+ "");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println("Error de Conectividad");
                        }
                    });
            jsonObjectRequest.setShouldCache(false);
            RequestAPI.getInstance(this).add(jsonObjectRequest);
            return true;
        } else {
            return false;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}