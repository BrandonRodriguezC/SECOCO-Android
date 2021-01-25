package com.example.secoco.usuarios.erc_covid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.secoco.R;
import com.example.secoco.general.RequestAPI;
import com.example.secoco.usuarios.erc_covid.recyclerView.AdaptadorRecyclerNotificarCita;
import com.example.secoco.usuarios.erc_covid.recyclerView.Item;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ReporteNotificarCita extends AppCompatActivity implements View.OnClickListener {

    //Atributos
    private MenuItem menuItem;
    private CheckBox[] checkBoxs;
    private Button btnBuscarUsuarios, btnEnviarMasivo;
    private ArrayList<Item> items;
    private RecyclerView recyclerView;
    private AdaptadorRecyclerNotificarCita adaptador;
    private CardView cardViewFiltro;
    private ConstraintLayout cnsActivity;
    private EditText txtFechaCita;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte_notificar_cita);
        getSupportActionBar().setTitle(R.string.reporteNotificacion_txt_titulo);

        //Inicialización de Atributos
        this.cnsActivity = (ConstraintLayout) findViewById(R.id.cns_a);

        this.cardViewFiltro = (CardView) findViewById(R.id.card_filtro);
        this.checkBoxs = new CheckBox[]{(CheckBox) findViewById(R.id.ch_contacto_sospechoso),
                (CheckBox) findViewById(R.id.ch_fiebre),
                (CheckBox) findViewById(R.id.ch_dif_respirar),
                (CheckBox) findViewById(R.id.ch_fatiga),
                (CheckBox) findViewById(R.id.ch_dis_olfato_sabor),
                (CheckBox) findViewById(R.id.ch_tos)};

        this.txtFechaCita = (EditText) findViewById(R.id.txt_fecha_cita);
        this.btnBuscarUsuarios = (Button) findViewById(R.id.btn_buscar_usuarios);
        this.btnEnviarMasivo = (Button) findViewById(R.id.btn_enviar_masivo);

        this.recyclerView = (RecyclerView) findViewById(R.id.recycler_usuarios);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.items = new ArrayList<>();
        this.items.add(new Item(0, 0));
        this.items.add(new Item(0, 0));
        this.items.add(new Item(0, 0));

        this.adaptador = new AdaptadorRecyclerNotificarCita(this, items);
        this.recyclerView.setAdapter(adaptador);

        //Acciones de Botones
        this.btnBuscarUsuarios.setOnClickListener(this);
        this.btnEnviarMasivo.setOnClickListener(this);
        this.txtFechaCita.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == btnBuscarUsuarios.getId()) {
            if (!txtFechaCita.getText().toString().equals("")) {
                obtenerPersonasconSintomas(tomarSintomas(), 100);
                visibilidadTarjetaFiltro(false);
            } else {
                Toast.makeText(ReporteNotificarCita.this,
                        "La Fecha no fue seleccionada ", Toast.LENGTH_SHORT).show();
            }
        } else if (view.getId() == btnEnviarMasivo.getId()) {
            String busqueda = tomarSintomas();
            if (!busqueda.equals("000000")) {
                JSONObject request = new JSONObject();
                try {
                    request.put("E", busqueda);
                    request.put("F", txtFechaCita.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                        "https://secocobackend.glitch.me/ENVIAR-CORREO-USUARIOS-NOTIFICAR-CITA",
                        request,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    boolean puedoEnviar = response.getInt("E") == 1;
                                    if (puedoEnviar) {
                                        Toast.makeText(ReporteNotificarCita.this, "Los correos han sido enviados"
                                                , Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ReporteNotificarCita.this, "Error al enviar los correos"
                                                , Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(ReporteNotificarCita.this, "Error de Conexión"
                                        , Toast.LENGTH_SHORT).show();
                            }
                        });
                jsonObjectRequest.setShouldCache(false);
                RequestAPI.getInstance(this).add(jsonObjectRequest);
            }

        } else if (view.getId() == txtFechaCita.getId()) {
            Calendar fechaActual = Calendar.getInstance();
            int dia = fechaActual.get(Calendar.DAY_OF_MONTH), mes = fechaActual.get(Calendar.MONTH), anio = fechaActual.get(Calendar.YEAR);

            fechaActual.set(Calendar.HOUR, 0);
            fechaActual.set(Calendar.MINUTE, 0);
            fechaActual.set(Calendar.SECOND, 0);

            DatePickerDialog fecha = new DatePickerDialog(ReporteNotificarCita.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    Calendar fechaSeleccionada = Calendar.getInstance();
                    fechaSeleccionada.set(Calendar.YEAR, year);
                    fechaSeleccionada.set(Calendar.MINUTE, month);
                    fechaSeleccionada.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    if (!fechaActual.before(fechaSeleccionada)) {
                        Toast.makeText(ReporteNotificarCita.this, "La Fecha Seleccionada no es Valida ",
                                Toast.LENGTH_SHORT).show();
                        txtFechaCita.setText("");
                    } else {
                        DateFormat formatoFecha = new SimpleDateFormat("dd-MM-yyyy");
                        txtFechaCita.setText(formatoFecha.format(fechaSeleccionada.getTime()));
                    }
                }
            }, anio, mes, dia);
            fecha.show();
        }
    }

    public void visibilidadTarjetaFiltro(boolean mostrar) {
        if (cardViewFiltro.getVisibility() == View.GONE && mostrar) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                TransitionManager.beginDelayedTransition(cnsActivity, new AutoTransition());
            }
            cardViewFiltro.setVisibility(View.VISIBLE);
            this.menuItem.setIcon(R.drawable.icon_menu_filtro_cerrar);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                TransitionManager.beginDelayedTransition(cnsActivity, new AutoTransition());
            }
            cardViewFiltro.setVisibility(View.GONE);
            this.menuItem.setIcon(R.drawable.icon_menu_filtro_abrir);
        }
    }

    private void obtenerPersonasconSintomas(String sintomas, int limite) {
        ProgressDialog progressDialog = new ProgressDialog(ReporteNotificarCita.this);
        progressDialog.setCancelable(false);
        progressDialog.show();
        JSONObject request = new JSONObject();
        try {
            request.put("E", sintomas);
            request.put("L", limite);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                "https://secocobackend.glitch.me/SOLICITAR-USUARIOS-NOTIFICAR-CITA",
                request,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            items.clear();
                            JSONArray a = response.getJSONArray("listaUsuarios");
                            for (int i = 0; i < a.length(); i++) {
                                items.add(new Item(1, a.getJSONObject(i), txtFechaCita.getText().toString()));
                            }
                            actualizarItems();
                            progressDialog.dismiss();
                        } catch (JSONException e) {
                            Log.e("Error Cargar", "Problemas para transformar los datos");
                            progressDialog.dismiss();
                            Toast.makeText(ReporteNotificarCita.this, "Problemas para transformar los datos",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error de Conexión", "Problemas durante la petición");
                        progressDialog.dismiss();
                        Toast.makeText(ReporteNotificarCita.this, "Problemas durante la petición",
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );
        jsonObjectRequest.setShouldCache(true);
        RequestAPI.getInstance(ReporteNotificarCita.this).add(jsonObjectRequest);
    }

    private void actualizarItems() {
        if (items.size() == 0) {
            items.add(new Item(0, 1));
            items.add(new Item(0, 1));
        }
        adaptador.notifyDataSetChanged();
    }

    private String tomarSintomas() {
        String busqueda = "";
        busqueda = checkBoxs[0].isChecked() ? busqueda + "1" : busqueda + "0";
        busqueda = checkBoxs[1].isChecked() ? busqueda + "1" : busqueda + "0";
        busqueda = checkBoxs[2].isChecked() ? busqueda + "1" : busqueda + "0";
        busqueda = checkBoxs[3].isChecked() ? busqueda + "1" : busqueda + "0";
        busqueda = checkBoxs[4].isChecked() ? busqueda + "1" : busqueda + "0";
        busqueda = checkBoxs[5].isChecked() ? busqueda + "1" : busqueda + "0";
        return busqueda;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notificar_cita, menu);
        this.menuItem = menu.getItem(0);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == this.menuItem.getItemId()) {
            visibilidadTarjetaFiltro(true);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}