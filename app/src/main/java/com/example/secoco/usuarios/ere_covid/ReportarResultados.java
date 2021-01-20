package com.example.secoco.usuarios.ere_covid;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.secoco.Ingreso;
import com.example.secoco.R;
import com.example.secoco.Registro;
import com.example.secoco.general.RequestAPI;
import com.example.secoco.general.ServicioUbicacion;
import com.example.secoco.usuarios.persona.Mapa;
import com.example.secoco.usuarios.persona.PersonaInicio;
import com.example.secoco.usuarios.persona.Sintomas;
import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.TestOnly;
import org.json.JSONException;
import org.json.JSONObject;

public class ReportarResultados extends AppCompatActivity implements View.OnClickListener {
    private Button button;
    private EditText tf_reporte_resultados_Cedula;
    private Spinner spn_reporte_resultados_TipoID, spn_reporte_resultados_Resultado;
    private String resultadoAbr;
    //BARRA
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportar_resultados);
        getSupportActionBar().setTitle("Reporte de resultados COVID-19");
        this.button = (Button) findViewById(R.id.btn_reporte_resultados_Envio);
        //Acción de Botones
        this.button.setOnClickListener(this);


    }
    @Override
    public void onClick(View view) {
        tf_reporte_resultados_Cedula =(EditText) findViewById(R.id.tf_reporte_resultados_Cedula);
        spn_reporte_resultados_TipoID = (Spinner) findViewById(R.id.spn_reporte_resultados_TipoID);
        spn_reporte_resultados_Resultado = (Spinner) findViewById(R.id.spn_reporte_resultados_Resultado);

        String tipoid= spn_reporte_resultados_TipoID.getSelectedItem().toString();
        String cedula= tf_reporte_resultados_Cedula.getText().toString();
        String resultado= spn_reporte_resultados_Resultado.getSelectedItem().toString();

        resultadoAbr="";
        if (resultado.equals("Activo")){
            resultadoAbr="A";
        }else if (resultado.equals("Inactivo")){
            resultadoAbr="I";
        }else if (resultado.equals("Solicitado")){
            resultadoAbr="S";
        }else if (resultado.equals("Pendiente")){
            resultadoAbr="P";
        }else if (resultado.equals("Examen no tomado")){
            resultadoAbr="N";
        }
        //ENVIO
        JSONObject request = new JSONObject();
        try {
            request.put("tipoID", tipoid);
            request.put("ID", cedula);
            request.put("resultado", resultadoAbr);
        }
        catch (JSONException e) {
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
                            Toast.makeText(ReportarResultados.this, rt, Toast.LENGTH_SHORT ).show();
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

}

