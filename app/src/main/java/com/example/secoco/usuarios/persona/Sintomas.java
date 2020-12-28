package com.example.secoco.usuarios.persona;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.secoco.R;

public class Sintomas extends AppCompatActivity implements View.OnClickListener{
    private Button button;
    private Spinner spinner4, spinner5, spinner6, spinner7, spinner8, spinner9;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sintomas);

        //Inicialización de Atributos
        this.button = (Button) findViewById(R.id.button);

        //Acción de Botones
        this.button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        spinner4 = (Spinner) findViewById(R.id.spinner4);
        spinner5 = (Spinner) findViewById(R.id.spinner5);
        spinner6 = (Spinner) findViewById(R.id.spinner6);
        spinner7 = (Spinner) findViewById(R.id.spinner7);
        spinner8 = (Spinner) findViewById(R.id.spinner8);
        spinner9 = (Spinner) findViewById(R.id.spinner9);
        String resultado="";
        if (view.getId() == this.button.getId()) {
            //pregunta1
            if ( spinner4.getSelectedItemPosition()==0 ){
                resultado+=1;
            }else {
                resultado+=0;
            }
            //pregunta2
            if ( spinner5.getSelectedItemPosition()==0 ){
                resultado+=1;
            }else {
                resultado+=0;
            }
            //pregunta3
            if ( spinner6.getSelectedItemPosition()==0 ){
                resultado+=1;
            }else {
                resultado+=0;
            }
            //pregunta4
            if ( spinner7.getSelectedItemPosition()==0 ){
                resultado+=1;
            }else {
                resultado+=0;
            }
            //pregunta5
            if ( spinner8.getSelectedItemPosition()==0 ){
                resultado+=1;
            }else {
                resultado+=0;
            }
            //pregunta6
            if ( spinner9.getSelectedItemPosition()==0 ){
                resultado+=1;
            }else {
                resultado+=0;
            }

            Toast.makeText(this,resultado,Toast.LENGTH_SHORT).show();

            //Envio

        }
    }
}