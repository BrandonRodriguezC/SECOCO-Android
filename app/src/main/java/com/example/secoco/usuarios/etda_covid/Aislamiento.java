package com.example.secoco.usuarios.etda_covid;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
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
import com.example.secoco.usuarios.etda_covid.recyclerViewAis.AdapterAis;
import com.example.secoco.usuarios.etda_covid.recyclerViewAis.ItemAis;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Aislamiento extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private AdapterAis adaptador;
    private ArrayList<ItemAis> itemAis;
    private Button btnEnviar;

    private Spinner spinner;

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
        btnEnviar = (Button) findViewById(R.id.boton_ais);

        btnEnviar.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == btnEnviar.getId()) {
            spinner = (Spinner) findViewById(R.id.spiner_localidad);
            JSONObject request = new JSONObject();
            String locali_numero = getResources().getStringArray(R.array.LocalidadesIdentificador)[spinner.getSelectedItemPosition()];
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
                                Toast.makeText(view.getContext(), "La zona se ha filtrado", Toast.LENGTH_SHORT).show();

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
                            Toast.makeText(view.getContext(), "Error de Conexión", Toast.LENGTH_SHORT).show();
                        }
                    });
            jsonObjectRequest.setShouldCache(false);
            RequestAPI.getInstance(view.getContext()).add(jsonObjectRequest);
        }
    }
}