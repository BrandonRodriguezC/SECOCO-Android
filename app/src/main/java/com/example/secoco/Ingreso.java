package com.example.secoco;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
        Button btn =(Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMap();
            }
        });
    }
    public void openMap(){
        Intent intent= new Intent(this, Map.class);
        startActivity(intent);
    }
    public void registrar(View view){
        Toast.makeText(this, "Registrar", Toast.LENGTH_SHORT).show();
        Intent registro = new Intent(this, Registro.class);
        startActivity(registro);
        //finish();
    }
}