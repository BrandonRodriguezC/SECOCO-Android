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
import android.widget.EditText;
import android.widget.Spinner;
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
import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;
import static com.mapbox.mapboxsdk.style.expressions.Expression.match;
import static com.mapbox.mapboxsdk.style.expressions.Expression.stop;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textField;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textOffset;

public class ReporteZona extends AppCompatActivity implements
        OnMapReadyCallback, MapboxMap.OnMapClickListener, View.OnClickListener {

    private static final String SOURCE_ID = "SOURCE_ID";
    private static final String PROPIEDAD_ICONO = "ICONOS";
    private static final String LAYER_ID = "LAYER_ID";
    private String ICONO_CUARENTENA = "ICONO_CUARENTENA", ICONO_NO_CUARENTENA = "ICONO_NO_CUARENTENA", Z, C;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private List<Feature> puntosLocalidades;
    private Feature puntoSeleccionado;
    private TextView lblTitulo, lblActivos, lblInactivos, lblSolicitado, lblPendientes, lblExamenNoTomado, lblTotal;
    private Button btnEstadoLocalidad;
    private CardView cardViewZona;
    private ConstraintLayout consActivity;
    private double P;

    //Reporte Resultado
    private Button btnReportarResultado, btnMostrarReporteResultado;
    private EditText tf_reporte_resultados_Cedula;
    private Spinner spn_reporte_resultados_TipoID, spn_reporte_resultados_Resultado;
    private String resultadoAbr;
    private CardView cardViewResultado;


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
        //Reporte Resultado
        this.btnReportarResultado = (Button) findViewById(R.id.btn_reporte_resultados_Envio);
        this.tf_reporte_resultados_Cedula = (EditText) findViewById(R.id.tf_reporte_resultados_Cedula);
        this.spn_reporte_resultados_TipoID = (Spinner) findViewById(R.id.spn_reporte_resultados_TipoID);
        this.spn_reporte_resultados_Resultado = (Spinner) findViewById(R.id.spn_reporte_resultados_Resultado);
        this.btnMostrarReporteResultado = (Button) findViewById(R.id.btn_mostrar_reporte_resultado);


        this.consActivity = (ConstraintLayout) findViewById(R.id.cns_reporte_zona);
        this.cardViewZona = (CardView) findViewById(R.id.card_zona);
        this.cardViewResultado = (CardView) findViewById(R.id.card_reporte_resultados);

        //Acción Botones
        this.btnEstadoLocalidad.setOnClickListener(this);
        //Acción de Botones - Reporte Resultado
        this.btnReportarResultado.setOnClickListener(this);
        this.btnMostrarReporteResultado.setOnClickListener(this);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        /*mapboxMap.setMaxZoomPreference(11.221700784018922);*/
        mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/mapbox/cjf4m44iw0uza2spb3q0a7s41"), new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                mapboxMap.addOnMapClickListener(ReporteZona.this);
                style.addImage(ICONO_CUARENTENA, BitmapFactory.decodeResource(
                        ReporteZona.this.getResources(), R.drawable.c4));
                style.addImage(ICONO_NO_CUARENTENA, BitmapFactory.decodeResource(
                        ReporteZona.this.getResources(), R.drawable.nc4));
                style.addSource(new GeoJsonSource(SOURCE_ID));
                style.addLayer(new SymbolLayer(LAYER_ID, SOURCE_ID).withProperties(
                        iconImage(match(
                                get(PROPIEDAD_ICONO), literal(ICONO_CUARENTENA),
                                stop(ICONO_NO_CUARENTENA, ICONO_NO_CUARENTENA),
                                stop(ICONO_CUARENTENA, ICONO_CUARENTENA))),
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

    public void visibilidadTarjetaFiltro(boolean visibilidad, String tarjeta) {
        if (visibilidad) {
            if (tarjeta.equals("Reporte Zona") && cardViewZona.getVisibility() == View.GONE) {
                transicion();
                btnMostrarReporteResultado.setVisibility(View.GONE);
                cardViewZona.setVisibility(View.VISIBLE);
            } else if (tarjeta.equals("Reporte Resultado") && cardViewResultado.getVisibility() == View.GONE) {
                transicion();
                cardViewResultado.setVisibility(View.VISIBLE);
            }
        } else {
            if (tarjeta.equals("Reporte Zona") && cardViewZona.getVisibility() == View.VISIBLE) {
                transicion();
                cardViewZona.setVisibility(View.GONE);
                btnMostrarReporteResultado.setVisibility(View.VISIBLE);
            } else if (tarjeta.equals("Reporte Resultado") && cardViewResultado.getVisibility() == View.VISIBLE) {
                transicion();
                cardViewResultado.setVisibility(View.GONE);
            }
        }
    }

    private void transicion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TransitionManager.beginDelayedTransition(consActivity, new AutoTransition());
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
                                if (zona.getString("C").equals("A")) {
                                    puntoLocalidad.addStringProperty(PROPIEDAD_ICONO, ICONO_CUARENTENA);
                                } else {
                                    puntoLocalidad.addStringProperty(PROPIEDAD_ICONO, ICONO_NO_CUARENTENA);
                                }

                                puntosLocalidades.add(puntoLocalidad);
                            }
                            cargarZonasLocal();
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

        jsonObjectRequest.setShouldCache(false);
        RequestAPI.getInstance(this).add(jsonObjectRequest);
    }

    private void cargarZonasLocal() {
        GeoJsonSource geoJsonSource = mapboxMap.getStyle().getSourceAs(SOURCE_ID);
        geoJsonSource.setGeoJson(FeatureCollection.fromFeatures(puntosLocalidades));
    }

    //Agregar un ProcessDialog
    private boolean accionIcono(PointF punto) {
        List<Feature> puntos = mapboxMap.queryRenderedFeatures(punto, LAYER_ID);
        if (!puntos.isEmpty()) {
            visibilidadTarjetaFiltro(false, "Reporte Resultado");
            visibilidadTarjetaFiltro(false, "Reporte Zona");
            JSONObject request = new JSONObject();
            this.puntoSeleccionado = puntos.get(0);
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

                                cambiarColor(porcActivos, lblActivos, false);
                                cambiarColor(porcInactivos, lblInactivos, true);
                                cambiarColor(porcSolicitados, lblSolicitado, true);
                                cambiarColor(porcPendientes, lblPendientes, false);
                                cambiarColor(porcNoExamen, lblExamenNoTomado, false);

                                lblTitulo.setText(puntos.get(0).getProperty("N").getAsString());
                                lblActivos.setText(response.getInt("A") + " - " + format.format((porcActivos * 100)) + "%");
                                lblInactivos.setText(response.getInt("I") + " - " + format.format((porcInactivos * 100)) + "%");
                                lblSolicitado.setText(response.getInt("S") + " - " + format.format((porcSolicitados * 100)) + "%");
                                lblPendientes.setText(response.getInt("P") + " - " + format.format((porcPendientes * 100)) + "%");
                                lblExamenNoTomado.setText(response.getInt("N") + " - " + format.format((porcNoExamen * 100)) + "%");
                                lblTotal.setText(total + " - 100%");
                                visibilidadTarjetaFiltro(true, "Reporte Zona");
                                Z = puntos.get(0).getProperty("I").getAsString();
                                C = porcActivos > 0.5 ? "A" : "I";
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
            visibilidadTarjetaFiltro(false, "Reporte Zona");
            visibilidadTarjetaFiltro(false, "Reporte Resultado");
            return false;
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == btnEstadoLocalidad.getId()) {
            JSONObject request = new JSONObject();
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
                            try {
                                if (response.getInt("E") == 1) {
                                    int indexLocalidad = -1, i = 0;
                                    boolean encontrado = false;
                                    while (!encontrado && i < puntosLocalidades.size()) {
                                        if (puntosLocalidades.get(i).getProperty("I").getAsString().equals(Z)) {
                                            indexLocalidad = i;
                                            encontrado = true;
                                        }
                                        i++;
                                    }
                                    if (indexLocalidad != -1) {
                                        if (C.equals("A")) {
                                            puntosLocalidades.get(indexLocalidad).addStringProperty(PROPIEDAD_ICONO, ICONO_CUARENTENA);
                                        } else {
                                            puntosLocalidades.get(indexLocalidad).addStringProperty(PROPIEDAD_ICONO, ICONO_NO_CUARENTENA);
                                        }
                                    }
                                    cargarZonasLocal();
                                    Toast.makeText(ReporteZona.this, "Se ha reportado a los residentes", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

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
        } else if (view.getId() == btnReportarResultado.getId()) {
            if (!tf_reporte_resultados_Cedula.getText().toString().equals("")) {
                String tipoid = spn_reporte_resultados_TipoID.getSelectedItem().toString();
                String cedula = tf_reporte_resultados_Cedula.getText().toString();
                String resultado = spn_reporte_resultados_Resultado.getSelectedItem().toString();

                resultadoAbr = "";
                if (resultado.equals("Activo")) {
                    resultadoAbr = "A";
                } else if (resultado.equals("Inactivo")) {
                    resultadoAbr = "I";
                } else if (resultado.equals("Solicitado")) {
                    resultadoAbr = "S";
                } else if (resultado.equals("Pendiente")) {
                    resultadoAbr = "P";
                } else if (resultado.equals("Examen no tomado")) {
                    resultadoAbr = "N";
                }
                //ENVIO
                JSONObject request = new JSONObject();
                try {
                    request.put("tipoID", tipoid);
                    request.put("ID", cedula);
                    request.put("resultado", resultadoAbr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                        "https://secocobackend.glitch.me/RESULTADO-EXAMEN",
                        request,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String rt = response.getString("X");
                                    Toast.makeText(ReporteZona.this, rt, Toast.LENGTH_SHORT).show();
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
            } else {
                tf_reporte_resultados_Cedula.setError("Cédula Requerida");
            }
        } else if (view.getId() == btnMostrarReporteResultado.getId()) {
            if (cardViewResultado.getVisibility() == View.GONE) {
                visibilidadTarjetaFiltro(true, "Reporte Resultado");
            } else if (cardViewResultado.getVisibility() == View.VISIBLE) {
                visibilidadTarjetaFiltro(false, "Reporte Resultado");
            }
        }
    }


    private void cambiarColor(double porcentaje, TextView text, boolean reverso) {
        if (porcentaje < 0.25) {
            if (!reverso)
                text.setTextColor(getResources().getColor(R.color.bajo));
            else
                text.setTextColor(getResources().getColor(R.color.alto));
        } else if (porcentaje >= 0.25 && porcentaje < 0.5) {
            if (!reverso)
                text.setTextColor(getResources().getColor(R.color.medio_bajo));
            else
                text.setTextColor(getResources().getColor(R.color.medio_alto));
        } else if (porcentaje >= 0.5 && porcentaje < 0.75) {
            if (!reverso)
                text.setTextColor(getResources().getColor(R.color.medio_alto));
            else
                text.setTextColor(getResources().getColor(R.color.medio_bajo));
        } else if (porcentaje >= 0.75 && porcentaje <= 1) {
            if (!reverso)
                text.setTextColor(getResources().getColor(R.color.alto));
            else
                text.setTextColor(getResources().getColor(R.color.bajo));
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