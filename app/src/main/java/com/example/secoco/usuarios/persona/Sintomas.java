package com.example.secoco.usuarios.persona;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.secoco.Ingreso;
import com.example.secoco.R;
import com.example.secoco.general.ServicioUbicacion;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.FirebaseDatabase;

public class Sintomas extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private Button button;
    private Spinner spinner4, spinner5, spinner6, spinner7, spinner8, spinner9;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private String resultado;
    //BARRA ----------------------
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sintomas);

        //Inicialización de Atributos
        this.button = (Button) findViewById(R.id.button);

        //Acción de Botones
        this.button.setOnClickListener(this);

        //MENU LATERAL - RECORDAR IMPLEMENTS NavigationView.OnNavigationItemSelectedListener
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.bringToFront();
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
       if (item.toString().equals("Cerrar sesion")) {
            ServicioUbicacion.finalizarServicio(this);
            Intent login = new Intent(Sintomas.this, Ingreso.class);
            startActivity(login);
            finish();
        }else if (item.toString().equals("Mapa")) {
            Intent actividad = new Intent(Sintomas.this, Mapa.class);
           actividad.putExtra("USUARIO", getIntent().getStringExtra("USUARIO"));
           actividad.putExtra("NOMBRE", getIntent().getStringExtra("NOMBRE"));
           actividad.putExtra("ID", getIntent().getStringExtra("ID"));
           actividad.putExtra("FECHA_NACIMIENTO", getIntent().getStringExtra("FECHA_NACIMIENTO"));
           actividad.putExtra("CORREO", getIntent().getStringExtra("CORREO"));
           actividad.putExtra("LOCALIDAD", getIntent().getStringExtra("LOCALIDAD"));
           actividad.putExtra("DIRECCION", getIntent().getStringExtra("DIRECCION"));
           actividad.putExtra("SINTOMAS", getIntent().getStringExtra("SINTOMAS"));
           actividad.putExtra("RESULTADO", getIntent().getStringExtra("RESULTADO"));
            startActivity(actividad);
           finish();
        }else if (item.toString().equals("Desactivar ubicacion")){
            ServicioUbicacion.finalizarServicio(this);
        }else if (item.toString().equals("Perfil")){
           Intent actividad = new Intent(Sintomas.this, PersonaInicio.class);
           actividad.putExtra("USUARIO", getIntent().getStringExtra("USUARIO"));
           actividad.putExtra("NOMBRE", getIntent().getStringExtra("NOMBRE"));
           actividad.putExtra("ID", getIntent().getStringExtra("ID"));
           actividad.putExtra("FECHA_NACIMIENTO", getIntent().getStringExtra("FECHA_NACIMIENTO"));
           actividad.putExtra("CORREO", getIntent().getStringExtra("CORREO"));
           actividad.putExtra("LOCALIDAD", getIntent().getStringExtra("LOCALIDAD"));
           actividad.putExtra("DIRECCION", getIntent().getStringExtra("DIRECCION"));
           actividad.putExtra("SINTOMAS", getIntent().getStringExtra("SINTOMAS"));
           actividad.putExtra("RESULTADO", getIntent().getStringExtra("RESULTADO"));
           startActivity(actividad);
           finish();
       }
        return false;
    }
}