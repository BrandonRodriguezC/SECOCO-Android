package com.example.secoco;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

       /* DatabaseReference usuarios = ref;
        if (contraseña.equals(contraseñaV)) {
            Query queryToGetData = ref.child(nombreUsuario);
            //-----------VERIFICAR CEDULA INSCRITA-------------
            queryToGetData.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        usuarios.child(nombreUsuario).setValue(new Usuario(nombre, apellido, correo, id, contraseña, direccion, tipo_id, localidad, estado, fechaNacimiento));
                        Intent inicio = new Intent(Registro.this, PersonaInicio.class);
                        startActivity(inicio);
                        finish();
                    } else {
                        Toast.makeText(Registro.this, R.string.Error_Nombre_de_Usuario, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(Registro.this, R.string.Error_Base_de_Datos, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
        }
      */

    }


}