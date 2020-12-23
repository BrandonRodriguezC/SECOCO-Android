package com.example.secoco;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.DatePicker;
import android.app.DatePickerDialog;

import java.text.SimpleDateFormat;

public class Registro extends AppCompatActivity {

    //atributos
    private EditText txt_fecha_nacimiento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        getSupportActionBar().setTitle("Registro");

        this.txt_fecha_nacimiento = (EditText) findViewById(R.id.txt_fecha_nacimiento);

        this.txt_fecha_nacimiento.setOnClickListener(new View.OnClickListener() {@Override
        public void onClick(View view) {seleccionarFecha(); }});
    }

    public void seleccionarFecha() {

    }

}