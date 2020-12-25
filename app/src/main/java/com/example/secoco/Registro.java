package com.example.secoco;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.secoco.entities.Usuario;
import com.example.secoco.usuarios.persona.PersonaInicio;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Registro extends AppCompatActivity {

    //atributos
    private EditText txt_nombre, txt_apellido, txt_correo, txt_id,
            txt_fecha_nacimiento, txt_contrasena_r, txt_contrasena_rv, txt_direccion, txt_nombre_usuario;
    private Button boton_registro;
    private Spinner spinner, spinner2, spinner3;
    private Context context;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("usuarios/Naturales");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        getSupportActionBar().setTitle("Registro");


        //fecha nacimiento
        MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Seleccione su fecha de nacimiento");
        final MaterialDatePicker materialDatePicker = builder.build();
        this.txt_fecha_nacimiento = (EditText) findViewById(R.id.txt_fecha_nacimiento);
        this.txt_fecha_nacimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialDatePicker.show(getSupportFragmentManager(), "Date_Picker ");

            }
        });
        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                txt_fecha_nacimiento.setText(materialDatePicker.getHeaderText());
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

        DatabaseReference usuarios = ref;
        if (contraseña.equals(contraseñaV)) {
            Query queryToGetData = ref.child(nombreUsuario);

            queryToGetData.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        usuarios.child(nombreUsuario).setValue(new Usuario(nombre, apellido, correo, id, contraseña, direccion, tipo_id, localidad, estado, fechaNacimiento));
                        Intent inicio = new Intent(Registro.this, PersonaInicio.class);
                        startActivity(inicio);
                        finish();
                    } else {
                        Toast.makeText(Registro.this.context, R.string.Error_Nombre_de_Usuario, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(Registro.this.context, R.string.Error_Base_de_Datos, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
        }


    }


}