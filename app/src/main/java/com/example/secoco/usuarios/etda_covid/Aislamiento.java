package com.example.secoco.usuarios.etda_covid;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.secoco.R;
import com.example.secoco.general.RequestAPI;
import com.example.secoco.usuarios.erc_covid.ReporteNotificarCita;
import com.example.secoco.usuarios.erc_covid.recyclerView.AdaptadorRecyclerNotificarCita;
import com.example.secoco.usuarios.erc_covid.recyclerView.Item;
import com.example.secoco.usuarios.etda_covid.recyclerViewAis.AdapterAis;
import com.example.secoco.usuarios.etda_covid.recyclerViewAis.ItemAis;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Aislamiento extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private AdapterAis adaptador;
    private ArrayList<ItemAis> itemAis;
    private Button btnEnviar;

    private TextView usr_ais, nombre_ais, correo_ais, cedula_ais, fecha_ais;
    private JSONObject localidad;
    private Spinner spinner;
    private String locali;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Reporte aislamiento");
        setContentView(R.layout.activity_aislamiento);


        itemAis = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_aisla);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adaptador = new AdapterAis(itemAis);
        recyclerView.setAdapter(adaptador);

        spinner = (Spinner) findViewById(R.id.spiner_localidad);
        locali = spinner.getSelectedItem().toString();
        btnEnviar = (Button) findViewById(R.id.boton_ais);
        btnEnviar.setOnClickListener(this);


    }


    @Override
    public void onClick(View view) {
        if (view.getId() == btnEnviar.getId()) {
            nombre_ais = (TextView) findViewById(R.id.item_nombre_ais);
            correo_ais = (TextView) findViewById(R.id.item_correo_ais);
            cedula_ais = (TextView) findViewById(R.id.item_cedula_ais);
            usr_ais = (TextView) findViewById(R.id.item_usuario_ais);
            fecha_ais=(TextView) findViewById(R.id.item_fecha_ais);

            spinner = (Spinner) findViewById(R.id.spiner_localidad);
            locali = spinner.getSelectedItem().toString();
            String locali_numero ="";
            JSONObject request = new JSONObject();
            if(locali.equals("Usaquén")){
                locali_numero="1";
            }else if(locali.equals("Antonio Nariño")){
                locali_numero="15";}
            else if(locali.equals("Barrios Unidos")){
                locali_numero="12";}
            else if(locali.equals("Bosa")){
                locali_numero="7";}
            else if(locali.equals("Chapinero")){
                locali_numero="2";}
            else if(locali.equals("Ciudad Bolívar")){
                locali_numero="19";}
            else if(locali.equals("Engativá")){
                locali_numero="10";}
            else if(locali.equals("Fontibón")){
                locali_numero="9";}
            else if(locali.equals("Kennedy")){
                locali_numero="8";}
            else if(locali.equals("La Candelaria")){
                locali_numero="17";}
            else if(locali.equals("Los Mártires")){
                locali_numero="16";}
            else if(locali.equals("Puente Aranda")){
                locali_numero="14";}
            else if(locali.equals("Rafael Uribe Uribe")){
                locali_numero="18";}
            else if(locali.equals("San Cristóbal")){
                locali_numero="4";}
            else if(locali.equals("Santa Fe")){
                locali_numero="3";}
            else if(locali.equals("Suba")){
                locali_numero="11";}
            else if(locali.equals("Sumapaz")){
                locali_numero="1";}
            else if(locali.equals("Teusaquillo")){
                locali_numero="13";}
            else if(locali.equals("Tunjuelito")){
                locali_numero="6";}
            else if(locali.equals("Usme")){
                locali_numero="5";}
            try {
                request.put("Z", locali_numero);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    "https://secocobackend.glitch.me/MOVIMIENTO-EN-AISLAMIENTO",
                    request,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Toast.makeText(view.getContext(), "La zona se ha filtrado"
                                        , Toast.LENGTH_SHORT).show();

                                JSONArray z = response.getJSONArray("Z");
                                for (int i = 0; i < z.length(); i++) {
                                    itemAis.add(new ItemAis(z.getJSONObject(i)));
                                }
                                adaptador.notifyDataSetChanged();


                            } catch (JSONException e) {
                                e.printStackTrace();

                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(view.getContext(), "Error de Conexión"
                                    , Toast.LENGTH_SHORT).show();
                        }
                    });
            jsonObjectRequest.setShouldCache(false);
            RequestAPI.getInstance(view.getContext()).add(jsonObjectRequest);




        }
    }
}