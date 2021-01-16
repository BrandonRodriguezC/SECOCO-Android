package com.example.secoco;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.secoco.general.RequestAPI;
import com.example.secoco.usuarios.persona.PersonaInicio;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Registro extends AppCompatActivity {

    //atributos
    private EditText txt_nombre, txt_apellido, txt_correo, txt_id,
            txt_fecha_nacimiento, txt_contrasena_r, txt_contrasena_rv, txt_direccion, txt_nombre_usuario;
    private Button boton_registro;
    private Spinner spinner, spinner2, spinner3;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("usuarios/Naturales");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        getSupportActionBar().setTitle("Registro");

        //fecha nacimiento
        this.txt_fecha_nacimiento = (EditText) findViewById(R.id.txt_fecha_nacimiento);
        this.txt_fecha_nacimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar fechaActual = Calendar.getInstance();
                int dia = fechaActual.get(Calendar.DAY_OF_MONTH), mes = fechaActual.get(Calendar.MONTH), anio = fechaActual.get(Calendar.YEAR);

                DatePickerDialog fecha = new DatePickerDialog(Registro.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        fechaActual.set(Calendar.YEAR, year);
                        fechaActual.set(Calendar.MINUTE, month);
                        fechaActual.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            DateFormat formatoFecha = new SimpleDateFormat("dd-MM-yyyy");
                            txt_fecha_nacimiento.setText(formatoFecha.format(fechaActual.getTime()));

                    }
                }, anio, mes, dia);
                fecha.show();
            }
        });

        boton_registro = (Button) findViewById(R.id.boton_registro);
        this.boton_registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boton_registro();
            }
        });
    }



    public void boton_registro() {
        txt_nombre = (EditText) findViewById(R.id.txt_nombre);
        txt_apellido = (EditText) findViewById(R.id.txt_apellido);
        txt_correo = (EditText) findViewById(R.id.txt_correo);
        txt_id = (EditText) findViewById(R.id.txt_id);
        txt_contrasena_r = (EditText) findViewById(R.id.txt_contrasena_r);
        txt_contrasena_rv = (EditText) findViewById(R.id.txt_contrasena_rv);
        txt_direccion = (EditText) findViewById(R.id.txt_direccion);
        txt_nombre_usuario = (EditText) findViewById(R.id.txt_nombre_usuario);
        txt_fecha_nacimiento = (EditText) findViewById(R.id.txt_fecha_nacimiento);

        spinner = (Spinner) findViewById(R.id.spinner);
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        spinner3 = (Spinner) findViewById(R.id.spinner3);

        //textos
        String nombre = txt_nombre.getText().toString();
        String apellido = txt_apellido.getText().toString();
        String correo = txt_correo.getText().toString();
        String id = txt_id.getText().toString();
        String contraseña = txt_contrasena_r.getText().toString();
        String contraseñaV = txt_contrasena_rv.getText().toString();
        String fechaNacimiento = txt_fecha_nacimiento.getText().toString();
        String direccion = txt_direccion.getText().toString();
        String nombreUsuario = txt_nombre_usuario.getText().toString();
        //spinners
        String tipo_id = spinner.getSelectedItem().toString();
        String localidad = getResources().getStringArray(R.array.LocalidadesIdentificador)[spinner2.getSelectedItemPosition()];
        String estado = spinner3.getSelectedItem().toString();

        JSONObject request = new JSONObject();
        try {

            request.put("nombre", nombre);
            request.put("apellido", apellido);
            request.put("contraseña", contraseña);
            request.put("correo", correo);
            request.put("tipoID", tipo_id);
            request.put("ID", id);
            request.put("fechaNacimiento", fechaNacimiento);
            request.put("localidad", localidad);
            request.put("direccion", direccion);
            request.put("usuario", nombreUsuario);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                "https://secocobackend.glitch.me/REGISTRO",
                request,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String rta = response.getString("M");
                            if(rta.equals("Usuario añadido!")){
                                Intent inicio = new Intent(Registro.this, PersonaInicio.class);
                                inicio.putExtra("USUARIO", nombreUsuario);
                                startActivity(inicio);
                                finish();
                            }else{
                                Toast.makeText(Registro.this, rta , Toast.LENGTH_SHORT ).show();
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


}