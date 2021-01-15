package com.example.secoco.usuarios.ere_covid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.secoco.R;

public class EREInicio extends AppCompatActivity implements View.OnClickListener {

    //Atributos
    private TextView lblUsuario;
    private Button btnReporteZona;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ere_inicio);

        //Inicialización atributos
        this.lblUsuario = (TextView) findViewById(R.id.lbl_usuario);
        this.lblUsuario.setText(getIntent().getStringExtra("USUARIO"));
        this.btnReporteZona = (Button) findViewById(R.id.btn_reporte_zona);

        //Accion Botones
        this.btnReporteZona.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        if(view.getId() == btnReporteZona.getId()){
            Intent reporteZona = new Intent(EREInicio.this, ReporteZona.class);
            startActivity(reporteZona);
        }
    }
}