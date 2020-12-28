package com.example.secoco;

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

import com.example.secoco.usuarios.persona.PersonaInicio;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Ingreso extends AppCompatActivity implements View.OnClickListener {

    //Atributos
    private EditText txtUsuario, txtContrasena;
    private Button btnIngresar;
    private TextView lblRegistro, lblContrasena;
    private DatabaseReference baseDatos;
    private Spinner spTipoUsuario;
    private String[] opcionesSpinner;

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

        opcionesSpinner = new String[]{"Naturales", "Diagnostico", "Seguimiento", "Distrito"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Ingreso.this, android.R.layout.simple_spinner_dropdown_item, opcionesSpinner);
        this.spTipoUsuario.setAdapter(adapter);

        cargarCredenciales();

        //Accion a los botones
        this.btnIngresar.setOnClickListener(this);
        this.lblRegistro.setOnClickListener(this);
        this.lblContrasena.setOnClickListener(this);

        //Inicialización de Base de Datos
        this.baseDatos = FirebaseDatabase.getInstance().getReference("usuarios");
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

            //EVITAR CONSUMO DE RECURSOS
            Intent nuevaActividad = new Intent(Ingreso.this, PersonaInicio.class);
            nuevaActividad.putExtra("USUARIO", txt_usuario);
            startActivity(nuevaActividad);
            guardarCredenciales(txt_usuario, txt_contrasena, txt_tipo_usuario);
            //Para cerrar la activity y que no puedan volver al LOGIN despues de LOGGEARSE
            //finish();




            /*try {
                this.baseDatos.child(txt_tipo_usuario).child(txt_usuario).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String contrasena = snapshot.child("C").getValue().toString();
                            Intent nuevaActividad = null;
                            if (txt_contrasena.equals(contrasena)) {
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
                                //Toast.makeText(Ingreso.this, "Ingresando", Toast.LENGTH_SHORT).show();
                                if (nuevaActividad != null) {
                                    nuevaActividad.putExtra("USUARIO", txt_usuario);
                                    startActivity(nuevaActividad);
                                    guardarCredenciales(txt_usuario, txt_contrasena, txt_tipo_usuario);
                                    //Para cerrar la activity y que no puedan volver al LOGIN despues de LOGGEARSE
                                    //finish();
                                }
                            } else {
                                Toast.makeText(Ingreso.this, "Usuario y Contraseña Incorrecta", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(Ingreso.this, "Usuario y Contraseña Incorrecta", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        System.out.println("The read failed: " + error.getCode());
                    }
                });
            } catch (Exception e) {
                txtUsuario.setError("Caracteres Ingresados Invalidos ('.', '#', '$', '[', o ']')");
            }*/
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
        editor.commit();
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