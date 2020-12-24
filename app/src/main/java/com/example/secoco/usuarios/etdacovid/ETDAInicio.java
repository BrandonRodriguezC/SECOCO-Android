package com.example.secoco.usuarios.etdacovid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.secoco.R;

public class ETDAInicio extends AppCompatActivity {

    //Atributos
    private TextView lblUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etda_inicio);

        //Inicialización atributos
        this.lblUsuario = (TextView) findViewById(R.id.lbl_usuario);
        this.lblUsuario.setText(getIntent().getStringExtra("USUARIO"));
    }
}