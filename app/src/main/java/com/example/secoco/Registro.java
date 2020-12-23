package com.example.secoco;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

public class Registro extends AppCompatActivity {

    //atributos
    private EditText txt_fecha_nacimiento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        getSupportActionBar().setTitle("Registro");

        MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Seleccione su fecha de nacimiento");
        final MaterialDatePicker materialDatePicker= builder.build();
        this.txt_fecha_nacimiento = (EditText) findViewById(R.id.txt_fecha_nacimiento);

        this.txt_fecha_nacimiento.setOnClickListener(new View.OnClickListener() {@Override
        public void onClick(View view) {
            materialDatePicker.show(getSupportFragmentManager(), "Date_Picker ");

        }});

        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                txt_fecha_nacimiento.setText(materialDatePicker.getHeaderText());
            }
        });
    }


}