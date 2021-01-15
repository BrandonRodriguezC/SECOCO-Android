package com.example.secoco.usuarios.ere_covid;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.secoco.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ReportarResultados extends AppCompatActivity implements View.OnClickListener{
    Button  button;
    Spinner sp1, sp2;
    TextView tv;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportar_resultados);
        //Inicialización de Atributos
        this.button = (Button)  findViewById(R.id.btn_reporte_resultados_Envio);
        this.sp1 = (Spinner) findViewById(R.id.spn_reporte_resultados_TipoID);
        this.sp2 = (Spinner) findViewById(R.id.spn_reporte_resultados_Resultado);
        this.tv = (TextView) findViewById(R.id.tf_reporte_resultados_Cedula);
        //Acción de Botones
        this.button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String resultado= sp2.getSelectedItem().toString();
        String small=resultado.equals("Positivo")? "P": resultado.equals("Negativo")? "N": "S";
        String id= sp1.getSelectedItem().toString()+ " "+ tv.getText();

    }
}


