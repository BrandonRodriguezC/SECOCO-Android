package com.example.secoco.usuarios.ere_covid;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.secoco.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

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
        final String usuario []= {""};
        ref= (DatabaseReference) database.getReference("usuarios/Naturales");
       ref.orderByChild("I").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    usuario[0] =child.getKey();
                    database.getReference("usuarios/Naturales/" + usuario[0] + "/X").setValue(small);
                }
           }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

       if(small.equals("P")){


           /**
            *
            * 1354 - 10
            *
            * Ubicaciones (+)
            *       - fecha 1
            *           - hora 1
            *           - hora 2
            *           - hora 3
            *           - hora 4
            *       - fecha 2
            *           - hora 1
            *           - hora 2
            *           - hora 3
            *           - hora 4
            *       - fecha 3
            *           - hora 1
            *           - hora 2
            *           - hora 3
            *           - hora 4
            *       - fecha 4
            *           - hora 1
            *           - hora 2
            *           - hora 3
            *           - hora 4
            *
            *           HashList ht = new HashList();
            *
            *          for (fecha positivo) (el objetivo)
            *           orderByValue().equalTo("fecha1") ~> usuarios.list (recoger nombres de quienes estuvieron ese dia)
            *            H = hora_busqueda_incial, hora_busqueda_final , lat y long;
            *             for (each usuario de ese dia )
            *               ref./usuarios/naturales/XXXX/fecha1/.orderByValue().endAt(H.hora_busqueda_final).startAt(H.hora_busqueda_inicio) ~> ubicaciones.list (recoger nombres de quienes estuvieron ese dia y hora)
            *                  for each (each usuario de ese dia y hora)
            *                      double [] = LLAMAR_conversor_DIEGO_Lat&Long
            *                      if (H.lat - (5 metros) <= ubicacion.get().lat <=  H.lat + (5 metros)){
            *                           if (H.long - (5 metros) <= ubicacion.get().long <=  H.long + (5 metros)){
            *                                   ht.add(ubicacion.get().nombre());
            *                           }
            *                      }
            *           ------
            *           RecorrerLista_Y_Enviar(Consultar_correo_Diego(ht));
            * */

           ref= (DatabaseReference) database.getReference("ubicaciones/");
           ArrayList<Object> arr = new ArrayList<>();

           ref.addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot snapshot) {
                   for (DataSnapshot usuario : snapshot.getChildren()){
                       arr.add(usuario.getKey());
                   }
               }
               @Override
               public void onCancelled(@NonNull DatabaseError error) {

               }
           });

           ref= (DatabaseReference) database.getReference("ubicaciones/"+ usuario[0]);
           ref.limitToLast(15).addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(DataSnapshot dataSnapshot) {
                   for (DataSnapshot child : dataSnapshot.getChildren()) {
                       /*database.getReference("ubicaciones/").orderByValue().equalTo();*/

                   }
               }
               @Override
               public void onCancelled(DatabaseError databaseError) {

               }
           });
       }
    }
}

