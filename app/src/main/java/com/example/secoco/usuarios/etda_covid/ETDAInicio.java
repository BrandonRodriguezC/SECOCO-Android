package com.example.secoco.usuarios.etda_covid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.secoco.R;

public class ETDAInicio extends AppCompatActivity implements View.OnClickListener {

    //Atributos
    private TextView lblUsuario;
    private Button reporteAislamiento;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etda_inicio);

        this.lblUsuario = (TextView) findViewById(R.id.lbl_usuario);
        this.lblUsuario.setText(getIntent().getStringExtra("USUARIO"));

        this.reporteAislamiento = (Button) findViewById(R.id.boton_aislamiento);
        this.reporteAislamiento.setOnClickListener(this);
    }
    public void onClick(View view) {
            Intent ailamiento = new Intent(ETDAInicio.this, Aislamiento.class);
            startActivity(ailamiento);
    }
}