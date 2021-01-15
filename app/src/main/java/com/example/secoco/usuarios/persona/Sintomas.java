package com.example.secoco.usuarios.persona;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.secoco.R;
import com.google.firebase.database.FirebaseDatabase;

public class Sintomas extends AppCompatActivity implements View.OnClickListener {
    private Button button;
    private Spinner spinner4, spinner5, spinner6, spinner7, spinner8, spinner9;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private String resultado;


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
        resultado = "";
        if (view.getId() == this.button.getId()) {
            //Organización de Preguntas de la que más impacto tiene a la que menos impacto
            //segun https://www.who.int/es/emergencies/diseases/novel-coronavirus-2019/advice-for-public/q-a-coronaviruses
            //pregunta 6 ¿Ha estado en contacto con alguien diagnosticado o sospechoso de covid-19 en los últimos días?
            if (spinner9.getSelectedItemPosition() == 0) {
                resultado += 1;
            } else {
                resultado += 0;
            }
            //pregunta 3 ¿Ha tenido fiebre o temperatura mayor a 38*C en los últimos dias?
            if (spinner6.getSelectedItemPosition() == 0) {
                resultado += 1;
            } else {
                resultado += 0;
            }
            //pregunta 1 ¿Le ha faltado el aire o ha tenido dificultad para respirar?
            if (spinner4.getSelectedItemPosition() == 0) {
                resultado += 1;
            } else {
                resultado += 0;
            }
            //pregunta 2 ¿Se ha sentido últimamente más fatigado de lo usual?
            if (spinner5.getSelectedItemPosition() == 0) {
                resultado += 1;
            } else {
                resultado += 0;
            }
            //pregunta 5 ¿Ha notado disminución del olfato o del sabor de los alimentos?
            if (spinner8.getSelectedItemPosition() == 0) {
                resultado += 1;
            } else {
                resultado += 0;
            }
            //pregunta 4 ¿Ha tenido tos en los últimos dias?
            if (spinner7.getSelectedItemPosition() == 0) {
                resultado += 1;
            } else {
                resultado += 0;
            }

            Toast.makeText(this, resultado, Toast.LENGTH_SHORT).show();


           /* //Envio
            String usuario = getIntent().getStringExtra("USUARIO");
            //Toast.makeText(this, "Sintomas: "+getIntent().getStringExtra("USUARIO"), Toast.LENGTH_SHORT).show();
            DatabaseReference ref = database.getReference("usuarios/Naturales/" + usuario + "/E");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                    ref.setValue(resultado);
                    Toast.makeText(Sintomas.this, "Sintomás Actualizados", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NotNull DatabaseError databaseError) {
                    Toast.makeText(Sintomas.this, R.string.Error_Base_de_Datos, Toast.LENGTH_SHORT).show();
                }
            });*/
        }
    }
}