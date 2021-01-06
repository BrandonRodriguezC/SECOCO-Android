package com.example.secoco.usuarios.erc_covid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.secoco.R;
import com.example.secoco.entities.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ERCInicio extends AppCompatActivity implements View.OnClickListener {

    //Atributos
    private TextView lblUsuario;
    private Button btnNotificarCita;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_erc_inicio);

        //Inicialización atributos
        this.lblUsuario = (TextView) findViewById(R.id.lbl_usuario);
        this.lblUsuario.setText(getIntent().getStringExtra("USUARIO"));
        this.btnNotificarCita = (Button) findViewById(R.id.btn_notificar_cita);

        //Accion para los botones
        this.btnNotificarCita.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == btnNotificarCita.getId()) {
            Intent notificarCita = new Intent(ERCInicio.this, ReporteNotificarCita.class);
            notificarCita.putExtra("USUARIO", getIntent().getStringExtra("USUARIO"));
            startActivity(notificarCita);
        }

    }

}