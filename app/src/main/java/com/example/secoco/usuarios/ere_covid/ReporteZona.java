package com.example.secoco.usuarios.ere_covid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textField;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textOffset;

public class ReporteZona extends AppCompatActivity implements
        OnMapReadyCallback, MapboxMap.OnMapClickListener, View.OnClickListener {

    private static final String SOURCE_ID = "SOURCE_ID";
    private static final String RED_ICON_ID = "RED_ICON_ID";
    private static final String LAYER_ID = "LAYER_ID";
    private MapView mapView;
    private MapboxMap mapboxMap;
    private List<Feature> puntosLocalidades;
    private TextView lblTitulo, lblActivos, lblInactivos, lblSolicitado, lblPendientes, lblExamenNoTomado, lblTotal;
    private Button btnEstadoLocalidad;
    private CardView cardViewZona;
    private ConstraintLayout consActivity;
    private String Z, C;
    private double P;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(ReporteZona.this, getString(R.string.access_token));

        setContentView(R.layout.activity_reporte_zona);

        //Inicialización de Atributos
        this.puntosLocalidades = new ArrayList<>();
        this.lblTitulo = (TextView) findViewById(R.id.reporte_zona_txt_titulo);
        this.lblActivos = (TextView) findViewById(R.id.reporte_zona_txt_activos);
        this.lblInactivos = (TextView) findViewById(R.id.reporte_zona_txt_inactivos);
        this.lblSolicitado = (TextView) findViewById(R.id.reporte_zona_txt_solicitados);
        this.lblPendientes = (TextView) findViewById(R.id.reporte_zona_txt_pendientes);
        this.lblExamenNoTomado = (TextView) findViewById(R.id.reporte_zona_txt_examenNoTomado);
        this.lblTotal = (TextView) findViewById(R.id.reporte_zona_txt_total);
        this.btnEstadoLocalidad = (Button) findViewById(R.id.btn_estado_localidad);

        this.consActivity = (ConstraintLayout) findViewById(R.id.cns_reporte_zona);
        this.cardViewZona = (CardView) findViewById(R.id.card_zona);

        //Acción Botones
        this.btnEstadoLocalidad.setOnClickListener(this);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setMaxZoomPreference(11.221700784018922);
        mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/mapbox/cjf4m44iw0uza2spb3q0a7s41"), new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                mapboxMap.addOnMapClickListener(ReporteZona.this);
                style.addImage(RED_ICON_ID, BitmapFactory.decodeResource(
                        ReporteZona.this.getResources(), R.drawable.mapbox_marker_icon_default));
                style.addSource(new GeoJsonSource(SOURCE_ID));
                style.addLayer(new SymbolLayer(LAYER_ID, SOURCE_ID).withProperties(
                        iconImage(RED_ICON_ID),
                        iconAllowOverlap(true),
                        textOffset(new Float[]{0f, -2.5f}),
                        textIgnorePlacement(true),
                        textAllowOverlap(true),
                        textField(get("N"))
                ));
                //Realiza un request al servidor y trae las coordenadas de las localidades
                cargarZonas();
            }
        });
        this.mapboxMap.getUiSettings().setAttributionEnabled(false);
        this.mapboxMap.getUiSettings().setLogoEnabled(false);

    }

    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        return accionIcono(mapboxMap.getProjection().toScreenLocation(point));
    }

    public void visibilidadTarjetaFiltro(boolean visibilidad) {
        if (visibilidad) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                TransitionManager.beginDelayedTransition(consActivity, new AutoTransition());
            }
            cardViewZona.setVisibility(View.VISIBLE);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                TransitionManager.beginDelayedTransition(consActivity, new AutoTransition());
            }
            cardViewZona.setVisibility(View.GONE);
        }
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
                                        Point.fromLngLat(zona.getDouble("Lon"), zona.getDouble("Lat")));
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

    //Agregar un ProcessDialog
    private boolean accionIcono(PointF punto) {
        List<Feature> puntos = mapboxMap.queryRenderedFeatures(punto, LAYER_ID);
        if (!puntos.isEmpty()) {
            visibilidadTarjetaFiltro(false);
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
                            try {
                                DecimalFormat format = new DecimalFormat("#.##");
                                int total = response.getInt("A") + response.getInt("I")
                                        + response.getInt("S") + response.getInt("P")
                                        + response.getInt("N");
                                double divisor = (total == 0) ? 1.0 : (double) total;
                                double porcActivos = response.getInt("A") / divisor,
                                        porcInactivos = response.getInt("I") / divisor,
                                        porcSolicitados = response.getInt("S") / divisor,
                                        porcPendientes = response.getInt("P") / divisor,
                                        porcNoExamen = response.getInt("N") / divisor;

                                cambiarColor(porcActivos, lblActivos);
                                cambiarColor(porcInactivos, lblInactivos);
                                cambiarColor(porcSolicitados, lblSolicitado);
                                cambiarColor(porcPendientes, lblPendientes);
                                cambiarColor(porcNoExamen, lblExamenNoTomado);

                                lblTitulo.setText(puntos.get(0).getProperty("N").getAsString());
                                lblActivos.setText(response.getInt("A") + " - " + format.format((porcActivos * 100)) + "%");
                                lblInactivos.setText(response.getInt("I") + " - " + format.format((porcInactivos * 100)) + "%");
                                lblSolicitado.setText(response.getInt("S") + " - " + format.format((porcSolicitados * 100)) + "%");
                                lblPendientes.setText(response.getInt("P") + " - " + format.format((porcPendientes * 100)) + "%");
                                lblExamenNoTomado.setText(response.getInt("N") + " - " + format.format((porcNoExamen * 100)) + "%");
                                lblTotal.setText(total + " - 100%");
                                visibilidadTarjetaFiltro(true);
                                Z = puntos.get(0).getProperty("I").getAsString();
                                C = porcActivos > 50 ? "A" : "I";
                                P = porcActivos;
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
            visibilidadTarjetaFiltro(false);
            return false;
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == btnEstadoLocalidad.getId()) {
            JSONObject request = new JSONObject();
            System.out.println("Entro");
            try {
                request.put("PA", P);
                request.put("Z", Z);
                request.put("C", C);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    "https://secocobackend.glitch.me/REPORTAR-CUARENTENA-ZONA",
                    request,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(ReporteZona.this, "Se ha reportado a los residentes", Toast.LENGTH_SHORT).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println("Error de Conexión");
                        }
                    }
            );
            jsonObjectRequest.setShouldCache(false);
            RequestAPI.getInstance(this).add(jsonObjectRequest);
        }
    }


    private void cambiarColor(double porcentaje, TextView text) {
        if (porcentaje < 0.25) {
            text.setTextColor(getResources().getColor(R.color.bajo));
        } else if (porcentaje >= 0.25 && porcentaje < 0.5) {
            text.setTextColor(getResources().getColor(R.color.medio_bajo));
        } else if (porcentaje >= 0.5 && porcentaje < 0.75) {
            text.setTextColor(getResources().getColor(R.color.medio_alto));
        } else if (porcentaje >= 0.75 && porcentaje <= 1) {
            text.setTextColor(getResources().getColor(R.color.alto));
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