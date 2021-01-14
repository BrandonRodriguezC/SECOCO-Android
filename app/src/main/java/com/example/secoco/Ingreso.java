package com.example.secoco;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.secoco.general.RequestAPI;
import com.example.secoco.usuarios.erc_covid.ERCInicio;
import com.example.secoco.usuarios.ere_covid.EREInicio;
import com.example.secoco.usuarios.etda_covid.ETDAInicio;
import com.example.secoco.usuarios.persona.PersonaInicio;

import org.json.JSONException;
import org.json.JSONObject;

public class Ingreso extends AppCompatActivity implements View.OnClickListener {

    //Atributos
    private EditText txtUsuario, txtContrasena;
    private Button btnIngresar;
    private TextView lblRegistro, lblContrasena;
    //Eliminar
    //private DatabaseReference baseDatos;
    private Spinner spTipoUsuario;
    private String[] opcionesSpinner;
    private Intent nuevaActividad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingreso);

        //Inicialización atributos
        this.txtUsuario = (EditText) findViewById(R.id.txt_usuario);
        this.txtContrasena = (EditText) findViewById(R.id.txt_contrasena);
        this.spTipoUsuario = (Spinner) findViewById(R.id.sp_tipo_usuario);
        this.btnIngresar = (Button) findViewById(R.id.btn_ingresar);
        this.lblRegistro = (TextView) findViewById(R.id.lbl_registrar);
        this.lblContrasena = (TextView) findViewById(R.id.lbl_cambioContrasena);

        opcionesSpinner = new String[]{"Naturales", "Diagnóstico", "Seguimiento", "Distrito"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(Ingreso.this, android.R.layout.simple_spinner_dropdown_item, opcionesSpinner);
        this.spTipoUsuario.setAdapter(adapter);

        cargarCredenciales();

        //Accion a los botones
        this.btnIngresar.setOnClickListener(this);
        this.lblRegistro.setOnClickListener(this);
        this.lblContrasena.setOnClickListener(this);

        //Inicialización de Base de Datos
        //Eliminar
        //this.baseDatos = FirebaseDatabase.getInstance().getReference("usuarios");
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == this.btnIngresar.getId()) {
            ingresar();
        } else if (view.getId() == this.lblRegistro.getId()) {
            registrar();
        } else if (view.getId() == this.lblContrasena.getId()) {
            cambioContrasena();
        }
    }

    /*Los tipos de usuario son
            - Persona (persona natural (Paciente))
            - ERE-COVID (Entidad que Reporta Examenes (Diagnosticos) Covid )
            - ERC-COVID (Entidad que Reporta Contactos Covid)
            - ETDA-COVID (Entidad que Toma las Decisiones de Aislamiento)
    */
    public void ingresar() {
        String txt_usuario = txtUsuario.getText().toString();
        String txt_contrasena = txtContrasena.getText().toString();
        String txt_tipo_usuario = spTipoUsuario.getSelectedItem().toString();

        if (!txt_usuario.equals("") && !txt_contrasena.equals("")) {

            //------------------------------------EVITAR CONSUMO DE RECURSOS -----
            Intent nuevaActividad = null;
            if (txt_tipo_usuario.equals(opcionesSpinner[1])) {
                //Toast.makeText(Ingreso.this, "ERC-COVID", Toast.LENGTH_SHORT).show();
                nuevaActividad = new Intent(Ingreso.this, ERCInicio.class);
            } else if (txt_tipo_usuario.equals(opcionesSpinner[3])) {
                //Toast.makeText(Ingreso.this, "ERE-COVID", Toast.LENGTH_SHORT).show();
                nuevaActividad = new Intent(Ingreso.this, EREInicio.class);
            } else if (txt_tipo_usuario.equals(opcionesSpinner[2])) {
                //Toast.makeText(Ingreso.this, "ETDA-COVID", Toast.LENGTH_SHORT).show();
                nuevaActividad = new Intent(Ingreso.this, ETDAInicio.class);
            } else if (txt_tipo_usuario.equals(opcionesSpinner[0])) {
                //Toast.makeText(Ingreso.this, "Persona", Toast.LENGTH_SHORT).show();
                nuevaActividad = new Intent(Ingreso.this, PersonaInicio.class);
            }
            if (nuevaActividad != null) {
                nuevaActividad.putExtra("USUARIO", txt_usuario);
                startActivity(nuevaActividad);
                guardarCredenciales(txt_usuario, txt_contrasena, txt_tipo_usuario);
            }
            //-------------------------------------------------------------------------------------
            // Login con Request to Node.js
            /*ProgressDialog progressDialog = new ProgressDialog(Ingreso.this);
            progressDialog.setCancelable(false);
            progressDialog.show();
            JSONObject request = new JSONObject();
            nuevaActividad = null;
            try {
                request.put("U", txt_usuario);
                request.put("C", txt_contrasena);
                if (txt_tipo_usuario.equals(opcionesSpinner[0])) {
                    request.put("T", "U_NATURALES");
                    nuevaActividad = new Intent(Ingreso.this, PersonaInicio.class);
                } else if (txt_tipo_usuario.equals(opcionesSpinner[1])) {
                    request.put("T", "U_DIAGNOSTICO");
                    nuevaActividad = new Intent(Ingreso.this, ERCInicio.class);
                } else if (txt_tipo_usuario.equals(opcionesSpinner[2])) {
                    request.put("T", "U_SEGUIMIENTO");
                    nuevaActividad = new Intent(Ingreso.this, ETDAInicio.class);
                } else if (txt_tipo_usuario.equals(opcionesSpinner[3])) {
                    request.put("T", "U_DISTRITO");
                    nuevaActividad = new Intent(Ingreso.this, EREInicio.class);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    "https://secocobackend.glitch.me/INGRESO", request,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                boolean puedeIngresar = response.getInt("I") == 1;
                                if (puedeIngresar) {
                                    progressDialog.dismiss();
                                    if (nuevaActividad != null) {
                                        nuevaActividad.putExtra("USUARIO", txt_usuario);
                                        startActivity(nuevaActividad);
                                        guardarCredenciales(txt_usuario, txt_contrasena, txt_tipo_usuario);
                                        //Para cerrar la activity y que no puedan volver al LOGIN despues de LOGGEARSE
                                        //finish();
                                    }
                                } else {
                                    Toast.makeText(Ingreso.this, "Usuario y Contraseña Incorrecta", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            } catch (JSONException e) {
                                Toast.makeText(Ingreso.this, "Error de Conexión", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(Ingreso.this, "Error de Conexión", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
            );
            jsonObjectRequest.setShouldCache(false);
            RequestAPI.getInstance(this).add(jsonObjectRequest);
            */
        } else {
            if (txt_usuario.equals(""))
                txtUsuario.setError("Usuario Requerido");
            if (txt_contrasena.equals(""))
                txtContrasena.setError("Contraseña Requerida");
            //Toast.makeText(this, "Favor Ingrese los Campos", Toast.LENGTH_SHORT).show();

        }
    }

    private void guardarCredenciales(String txt_usuario, String txt_contrasena, String txt_tipo_usuario) {
        SharedPreferences preferences = getSharedPreferences("credenciales", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("usuario", txt_usuario);
        editor.putString("contraseña", txt_contrasena);
        editor.putString("tipoUsuario", txt_tipo_usuario);
        editor.apply();
    }

    private void cargarCredenciales() {
        SharedPreferences preferences = getSharedPreferences("credenciales", MODE_PRIVATE);
        txtUsuario.setText(preferences.getString("usuario", ""));
        txtContrasena.setText(preferences.getString("contraseña", ""));
        String tipoUsuario = preferences.getString("tipoUsuario", "");
        for (int i = 0; i < opcionesSpinner.length; i++) {
            if (opcionesSpinner[i].equals(tipoUsuario)) {
                spTipoUsuario.setSelection(i);
                break;
            }
        }
    }

    public void registrar() {
        Toast.makeText(this, "Registrar", Toast.LENGTH_SHORT).show();
        Intent registro = new Intent(this, Registro.class);
        startActivity(registro);
        finish();
    }

    public void cambioContrasena() {
        //Toca Agregarlo
        Toast.makeText(this, "Cambiar Contraseña", Toast.LENGTH_SHORT).show();
        Intent cambioContrasena = new Intent(this, CambioContrasena.class);
        startActivity(cambioContrasena);
    }

}