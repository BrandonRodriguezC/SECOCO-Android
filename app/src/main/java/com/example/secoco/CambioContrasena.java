package com.example.secoco;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.secoco.general.RequestAPI;

import org.json.JSONException;
import org.json.JSONObject;

public class CambioContrasena extends AppCompatActivity implements View.OnClickListener {

    //Atributos
    private EditText txtUsuario, txtNuevaContrasena, txtNuevaContrasenaConfirmar;
    private Button btnCambiarContrasena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambio_contrasena);

        //Inicialización de Atributos
        this.txtUsuario = (EditText) findViewById(R.id.txt_usuario);
        this.txtNuevaContrasena = (EditText) findViewById(R.id.txt_nueva_contrasena);
        this.txtNuevaContrasenaConfirmar = (EditText) findViewById(R.id.txt_nueva_contrasena_confirmar);
        this.btnCambiarContrasena = (Button) findViewById(R.id.btn_cambiar_contrasena);

        //Acción Botones
        this.btnCambiarContrasena.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == btnCambiarContrasena.getId()) {
            if (!txtUsuario.getText().toString().equals("") && !txtNuevaContrasena.getText().toString().equals("")
                    && !txtNuevaContrasenaConfirmar.getText().toString().equals("")) {
                String nuevaContrasena = txtNuevaContrasena.getText().toString();
                String nuevaContrasenaConfirmar = txtNuevaContrasenaConfirmar.getText().toString();
                if (nuevaContrasena.equals(nuevaContrasenaConfirmar)) {
                    JSONObject request = new JSONObject();
                    try {
                        request.put("U", txtUsuario.getText().toString());
                        request.put("C", nuevaContrasena);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                            "https://secocobackend.glitch.me/CAMBIAR-CONTRASENA",
                            request,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        int actualizacion = response.getInt("A");
                                        if (actualizacion == 1) {
                                            Toast.makeText(CambioContrasena.this, "Contraseña Actualizada", Toast.LENGTH_SHORT).show();
                                            Intent ingreso = new Intent(CambioContrasena.this, Ingreso.class);
                                            startActivity(ingreso);
                                            finish();
                                        } else if (actualizacion == 2) {
                                            txtUsuario.setError("El Usuario no Existe");
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(CambioContrasena.this, "Error de Conexión", Toast.LENGTH_SHORT).show();
                                }
                            });
                    jsonObjectRequest.setShouldCache(false);
                    RequestAPI.getInstance(this).add(jsonObjectRequest);
                } else {
                    txtNuevaContrasena.setError("Contraseñas no Coinciden");
                    txtNuevaContrasenaConfirmar.setError("Contraseñas no Coinciden");
                }
            } else {
                if (txtUsuario.getText().toString().equals("")) {
                    txtUsuario.setError("Usuario Requerido");
                }
                if (txtNuevaContrasena.getText().toString().equals("")) {
                    txtNuevaContrasena.setError("Contraseña Requerida");
                }
                if (txtNuevaContrasenaConfirmar.getText().toString().equals("")) {
                    txtNuevaContrasenaConfirmar.setError("Contraseña Requerida");
                }
            }
        }
    }
}