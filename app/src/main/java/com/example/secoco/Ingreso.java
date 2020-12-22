package com.example.secoco;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.secoco.usuarios.erccovid.ERCInicio;
import com.example.secoco.usuarios.erecovid.EREInicio;
import com.example.secoco.usuarios.etdacovid.ETDAInicio;
import com.example.secoco.usuarios.persona.Map;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Ingreso extends AppCompatActivity {

    //Atributos
    private EditText txtUsuario, txtContrasena;
    private Button btnIngresar;
    private TextView lblRegistro, lblContrasena;
    private DatabaseReference baseDatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingreso);

        //Inicialización atributos
        this.txtUsuario = (EditText) findViewById(R.id.txt_usuario);
        this.txtContrasena = (EditText) findViewById(R.id.txt_contrasena);
        this.btnIngresar = (Button) findViewById(R.id.btn_ingresar);
        this.lblRegistro = (TextView) findViewById(R.id.lbl_registrar);
        this.lblContrasena = (TextView) findViewById(R.id.lbl_cambioContrasena);

        //Accion a los botones
        this.btnIngresar.setOnClickListener(new View.OnClickListener() {@Override
            public void onClick(View view) {ingresar(); }});

        this.lblRegistro.setOnClickListener(new View.OnClickListener() {@Override
            public void onClick(View v) {registrar();}});

        this.lblContrasena.setOnClickListener(new View.OnClickListener() {@Override
            public void onClick(View v) {cambioContrasena();}});

        //Inicialización de Base de Datos
        this.baseDatos = FirebaseDatabase.getInstance().getReference();

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
        if (!txt_usuario.equals("") && !txt_contrasena.equals("")) {
            try {
                this.baseDatos.child("usuarios").child(txt_usuario).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String tipoUsuario = snapshot.child("tipo").getValue().toString();
                            String contrasena = snapshot.child("contraseña").getValue().toString();
                            Intent nuevaActividad = null;
                            if (txt_contrasena.equals(contrasena)) {
                                if (tipoUsuario.equals("ERC-COVID")) {
                                    //Toast.makeText(Ingreso.this, "ERC-COVID", Toast.LENGTH_SHORT).show();
                                    nuevaActividad = new Intent(Ingreso.this, ERCInicio.class);
                                } else if (tipoUsuario.equals("ERE-COVID")) {
                                    //Toast.makeText(Ingreso.this, "ERE-COVID", Toast.LENGTH_SHORT).show();
                                    nuevaActividad = new Intent(Ingreso.this, EREInicio.class);
                                } else if (tipoUsuario.equals("ETDA-COVID")) {
                                    //Toast.makeText(Ingreso.this, "ETDA-COVID", Toast.LENGTH_SHORT).show();
                                    nuevaActividad = new Intent(Ingreso.this, ETDAInicio.class);
                                } else if (tipoUsuario.equals("Persona")) {
                                    //Toast.makeText(Ingreso.this, "Persona", Toast.LENGTH_SHORT).show();
                                    nuevaActividad = new Intent(Ingreso.this, Map.class);
                                }
                                //Toast.makeText(Ingreso.this, "Ingresando", Toast.LENGTH_SHORT).show();
                                txtContrasena.setText("");
                                if (nuevaActividad != null) {
                                    nuevaActividad.putExtra("USUARIO", txt_usuario);
                                    startActivity(nuevaActividad);
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
            }
        } else {
            if (txtUsuario.getText().toString().equals(""))
                txtUsuario.setError("Usuario Requerido");
            if (txtContrasena.getText().toString().equals(""))
                txtContrasena.setError("Contraseña Requerida");
            //Toast.makeText(this, "Favor Ingrese los Campos", Toast.LENGTH_SHORT).show();
        }
    }

    public void registrar() {
        Toast.makeText(this, "Registrar", Toast.LENGTH_SHORT).show();
        Intent registro = new Intent(this, Registro.class);
        startActivity(registro);
        //finish();
    }

    public void cambioContrasena() {
        //Toca Agregarlo
        Toast.makeText(this, "Cambiar Contraseña", Toast.LENGTH_SHORT).show();
    }

}