package com.example.secoco.usuarios.persona;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.secoco.Ingreso;
import com.example.secoco.R;
import com.example.secoco.general.ServicioUbicacion;
import com.example.secoco.general.VariablesGenerales;
import com.google.android.material.navigation.NavigationView;

public class PersonaInicio extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    //Atributos
    private Button btnUbicacion;
    private Button btnMapa;

    int sintomasEntero;
    String usuario, nombre, id, fechaNacimiento, correo, localidad, direccion, sintomas, resultado;

    //BARRA ----------------------
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persona_inicio);
        sintomasEntero = 0;
        usuario = getIntent().getStringExtra("USUARIO");
        nombre = getIntent().getStringExtra("NOMBRE");
        id = getIntent().getStringExtra("ID");
        fechaNacimiento = getIntent().getStringExtra("FECHA_NACIMIENTO");
        correo = getIntent().getStringExtra("CORREO");
        localidad = getIntent().getStringExtra("LOCALIDAD");
        direccion = getIntent().getStringExtra("DIRECCION");

        sintomas = getIntent().getStringExtra("SINTOMAS");

        resultado = getIntent().getStringExtra("RESULTADO");

        TextView usuarioTV = (TextView) findViewById(R.id.persona_inicio_Usuario);
        TextView nombreTV = (TextView) findViewById(R.id.persona_inicio_Nombre);
        TextView idTV = (TextView) findViewById(R.id.persona_inicio_ID);
        TextView fechaNacimientoTV = (TextView) findViewById(R.id.persona_inicio_FechaNacimiento);
        TextView correoTV = (TextView) findViewById(R.id.persona_inicio_Correo);
        TextView localidadTV = (TextView) findViewById(R.id.persona_inicio_Localidad);
        TextView direccionTV = (TextView) findViewById(R.id.persona_inicio_Direccion);

        TextView contactoTV = (TextView) findViewById(R.id.persona_inicio_Contacto);
        TextView respriacionTV = (TextView) findViewById(R.id.persona_inicio_Respiracion);
        TextView fatigaTV = (TextView) findViewById(R.id.persona_inicio_Fatiga);
        TextView fiebreTV = (TextView) findViewById(R.id.persona_inicio_Fiebre);
        TextView tosTV = (TextView) findViewById(R.id.persona_inicio_Tos);
        TextView sentidosTV = (TextView) findViewById(R.id.persona_inicio_Sentidos);

        TextView resultadoTV = (TextView) findViewById(R.id.persona_inicio_Resultado);

        usuarioTV.setText(usuario);
        nombreTV.setText(nombre);
        idTV.setText(id);
        fechaNacimientoTV.setText(fechaNacimiento);
        correoTV.setText(correo);
        localidadTV.setText(localidad);
        direccionTV.setText(direccion);

        if (sintomas.charAt(0) == '1') {
            contactoTV.setText("Contacto");
        } else {
            sintomasEntero++;
            contactoTV.setText("");
        }
        if (sintomas.charAt(1) == '1') {
            respriacionTV.setText("Ahogo");
        } else {
            sintomasEntero++;
            respriacionTV.setText("");
        }
        if (sintomas.charAt(2) == '1') {
            fatigaTV.setText("Desaliento");
        } else {
            sintomasEntero++;
            fatigaTV.setText("");
        }
        if (sintomas.charAt(3) == '1') {
            fiebreTV.setText("Fiebre");
        } else {
            sintomasEntero++;
            fiebreTV.setText("");
        }
        if (sintomas.charAt(4) == '1') {
            tosTV.setText("Tos");
        } else {
            sintomasEntero++;
            tosTV.setText("");
        }
        if (sintomas.charAt(5) == '1') {
            sentidosTV.setText("Perdida de olfato y gusto");
        } else {
            sintomasEntero++;
            sentidosTV.setText("");
        }


        if (sintomasEntero == 6) {
            contactoTV.setText("No registra sintomas");
        }
        resultadoTV.setText(resultado);

        //Inicia a tomar y validar las coordenadas (latitud y longitud)
        iniciarReporteUbicacion();

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

    }

    /*-----------------------Metodos Necesarios para iniciar el reporte ubicación-----------------*/
    private void iniciarReporteUbicacion() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    VariablesGenerales.CODIGO_REQUEST_EXITOSO);
        } else {
            ServicioUbicacion.verificarGPS(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == VariablesGenerales.CODIGO_REQUEST_EXITOSO && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ServicioUbicacion.verificarGPS(this);
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            VariablesGenerales.CODIGO_REQUEST_EXITOSO);
                } else {
                    Toast.makeText(PersonaInicio.this, "Permisos de Acceso a la Ubicación Denegados",
                            Toast.LENGTH_SHORT).show();
                    Intent ingreso = new Intent(PersonaInicio.this, Ingreso.class);
                    startActivity(ingreso);
                    finish();
                }
            }
        }
    }
    /*--------------------------------------------------------------------------------------------*/

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.toString().equals("Sintomas")) {
            Intent actividad = new Intent(this, Sintomas.class);
            actividad.putExtra("USUARIO", usuario);
            actividad.putExtra("NOMBRE", nombre);
            actividad.putExtra("ID", id);
            actividad.putExtra("FECHA_NACIMIENTO", fechaNacimiento);
            actividad.putExtra("CORREO", correo);
            actividad.putExtra("LOCALIDAD", localidad);
            actividad.putExtra("DIRECCION", direccion);
            actividad.putExtra("SINTOMAS", sintomas);
            actividad.putExtra("RESULTADO", resultado);
            actividad.putExtra("ZONA", getIntent().getStringExtra("ZONA"));
            startActivity(actividad);
            finish();
        } else if (item.toString().equals("Cerrar sesion")) {
            ServicioUbicacion.finalizarServicio(this);
            Intent login = new Intent(PersonaInicio.this, Ingreso.class);
            startActivity(login);
            finish();
        } else if (item.toString().equals("Mapa")) {
            Intent actividad = new Intent(PersonaInicio.this, Mapa.class);
            actividad.putExtra("USUARIO", usuario);
            actividad.putExtra("NOMBRE", nombre);
            actividad.putExtra("ID", id);
            actividad.putExtra("FECHA_NACIMIENTO", fechaNacimiento);
            actividad.putExtra("CORREO", correo);
            actividad.putExtra("LOCALIDAD", localidad);
            actividad.putExtra("DIRECCION", direccion);
            actividad.putExtra("SINTOMAS", sintomas);
            actividad.putExtra("RESULTADO", resultado);
            actividad.putExtra("ZONA", getIntent().getStringExtra("ZONA"));
            startActivity(actividad);
            finish();
        } else if (item.toString().equals("Desactivar ubicacion")) {
            ServicioUbicacion.finalizarServicio(this);
        }

        return false;
    }


}