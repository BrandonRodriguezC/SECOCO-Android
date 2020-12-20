package com.example.secoco;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class Ingreso extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingreso);
    }

    public void ingresar(View view){
        Toast.makeText(this, "Ingresando", Toast.LENGTH_SHORT).show();
        //Comprobar que tipo de usuario y redirigir a su respectiva Activity
        //finish();

    }

    public void registrar(View view){
        Toast.makeText(this, "Registrar", Toast.LENGTH_SHORT).show();
        Intent registro = new Intent(this, Registro.class);
        startActivity(registro);
        //finish();
    }
}