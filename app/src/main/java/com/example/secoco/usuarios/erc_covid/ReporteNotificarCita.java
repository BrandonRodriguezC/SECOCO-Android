package com.example.secoco.usuarios.erc_covid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.secoco.R;
import com.example.secoco.entities.Usuario;
import com.example.secoco.general.Email;
import com.example.secoco.general.VariablesGenerales;
import com.example.secoco.usuarios.erc_covid.recyclerView.AdaptadorRecyclerNotificarCita;
import com.example.secoco.usuarios.erc_covid.recyclerView.Item;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ReporteNotificarCita extends AppCompatActivity implements View.OnClickListener {

    //Atributos
    private DatabaseReference baseDatos;
    private CheckBox [] checkBoxs;
    private Button btnBuscarUsuarios, btnEnviarMasivo, btnFiltro;
    private ArrayList<Item> items;
    private RecyclerView recyclerView;
    private AdaptadorRecyclerNotificarCita adaptador;
    private CardView cardViewFiltro;
    private ConstraintLayout cnsActivity;
    private String busqueda;
    private EditText txtFechaCita;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte_notificar_cita);
        getSupportActionBar().setTitle(R.string.reporteNotificacion_txt_titulo);

        //Inicialización de Atributos
        this.busqueda = "";
        this.cnsActivity = (ConstraintLayout) findViewById(R.id.cns_a);
        this.btnFiltro = (Button) findViewById(R.id.btn_filtro);

        this.cardViewFiltro = (CardView) findViewById(R.id.card_filtro);
        this.checkBoxs = new CheckBox[]{(CheckBox) findViewById(R.id.ch_contacto_sospechoso),
                (CheckBox) findViewById(R.id.ch_fiebre),
                (CheckBox) findViewById(R.id.ch_dif_respirar),
                (CheckBox) findViewById(R.id.ch_fatiga),
                (CheckBox) findViewById(R.id.ch_dis_olfato_sabor),
                (CheckBox) findViewById(R.id.ch_tos)};

        this.txtFechaCita = (EditText) findViewById(R.id.txt_fecha_cita);
        this.btnBuscarUsuarios = (Button) findViewById(R.id.btn_buscar_usuarios);
        this.btnEnviarMasivo = (Button) findViewById(R.id.btn_enviar_masivo);

        this.recyclerView = (RecyclerView) findViewById(R.id.recycler_usuarios);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.items = new ArrayList<Item>();
        this.items.add(new Item(0, 0, ReporteNotificarCita.this));
        this.items.add(new Item(0, 0, ReporteNotificarCita.this));

        this.adaptador = new AdaptadorRecyclerNotificarCita(items);
        this.recyclerView.setAdapter(adaptador);

        //Acciones de Botones
        this.btnBuscarUsuarios.setOnClickListener(this);
        this.btnEnviarMasivo.setOnClickListener(this);
        this.btnFiltro.setOnClickListener(this);
        this.txtFechaCita.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        if (view.getId() == btnBuscarUsuarios.getId()) {
            if (!txtFechaCita.getText().toString().equals("")) {
                //La busqueda sigue la prioridad de preguntas
                busqueda = "";
                busqueda = checkBoxs[0].isChecked() ? busqueda + "1" : busqueda + "0";
                busqueda = checkBoxs[1].isChecked() ? busqueda + "1" : busqueda + "0";
                busqueda = checkBoxs[2].isChecked() ? busqueda + "1" : busqueda + "0";
                busqueda = checkBoxs[3].isChecked() ? busqueda + "1" : busqueda + "0";
                busqueda = checkBoxs[4].isChecked() ? busqueda + "1" : busqueda + "0";
                busqueda = checkBoxs[5].isChecked() ? busqueda + "1" : busqueda + "0";
                obtenerPersonasconSintomas(busqueda, 100);

                visibilidadTarjetaFiltro(false);
            } else {
                Toast.makeText(ReporteNotificarCita.this,
                        "La Fecha no fue seleccionada ", Toast.LENGTH_SHORT).show();
            }
        } else if (view.getId() == btnEnviarMasivo.getId()){
            /* Prototipo de Correo Masivo
            ArrayList<String> correos = new ArrayList<String>();
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).getUsuario() != null) {
                    correos.add(items.get(i).getUsuario().M);
                }
            }
            String email[] = new String[correos.size()];
            email = correos.toArray(email);
            if (email.length > 0) {
                Email.enviarCorreoForeGround(view, email, "Citación para Prueba COVID-19", mensajeMasivo(view, txtFechaCita.getText().toString()));
            } else {
                Toast.makeText(ReporteNotificarCita.this, "Usuarios no disponibles", Toast.LENGTH_SHORT).show();
            }*/

            //Falta solicitar las credenciales del correo. Por el momento se envian con:
            String [] emailOrigen = VariablesGenerales.EMAIL_ORIGEN;
            for (int i = 0; i < items.size(); i++) {
                Usuario usuario = items.get(i).getUsuario();
                if (usuario != null) {
                    Email.enviarCorreoBackGround(view,
                            emailOrigen,
                            usuario.M, "Citación para Prueba COVID-19",
                                    Email.mensajePersonalizado(view,
                                            new String[]{usuario.N, usuario.I, txtFechaCita.getText().toString(),
                                                    usuario.E, usuario.D}),
                                    "Actualizar Examen", "usuarios/Naturales/" + items.get(i).getUsuarioKey() + "/X", "- S"
                    );
                }
            }
        } else if (view.getId() == btnFiltro.getId()) {
            visibilidadTarjetaFiltro(true);
        } else if (view.getId() == txtFechaCita.getId()) {
            Calendar fechaActual = Calendar.getInstance();
            int dia = fechaActual.get(Calendar.DAY_OF_MONTH), mes = fechaActual.get(Calendar.MONTH), anio = fechaActual.get(Calendar.YEAR);

            fechaActual.set(Calendar.HOUR, 0);
            fechaActual.set(Calendar.MINUTE, 0);
            fechaActual.set(Calendar.SECOND, 0);

            DatePickerDialog fecha = new DatePickerDialog(ReporteNotificarCita.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    Calendar fechaSeleccionada = Calendar.getInstance();
                    fechaSeleccionada.set(Calendar.YEAR, year);
                    fechaSeleccionada.set(Calendar.MINUTE, month);
                    fechaSeleccionada.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    if (!fechaActual.before(fechaSeleccionada)) {
                        Toast.makeText(ReporteNotificarCita.this, "La Fecha Seleccionada no es Valida ",
                                Toast.LENGTH_SHORT).show();
                        txtFechaCita.setText("");
                    } else {
                        DateFormat formatoFecha = new SimpleDateFormat("dd-MM-yyyy");
                        txtFechaCita.setText(formatoFecha.format(fechaSeleccionada.getTime()));
                    }
                }
            }, anio, mes, dia);
            fecha.show();
        }
    }

    public void visibilidadTarjetaFiltro(boolean mostrar) {
        if (cardViewFiltro.getVisibility() == View.GONE && mostrar) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                TransitionManager.beginDelayedTransition(cnsActivity, new AutoTransition());
            }
            cardViewFiltro.setVisibility(View.VISIBLE);
            btnFiltro.setBackgroundResource(R.drawable.icon_menu_filtro_cerrar);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                TransitionManager.beginDelayedTransition(cnsActivity, new AutoTransition());
            }
            cardViewFiltro.setVisibility(View.GONE);
            btnFiltro.setBackgroundResource(R.drawable.icon_menu_filtro_abrir);
        }
    }

    private void obtenerPersonasconSintomas(String sintomas, int limite) {
        this.baseDatos = FirebaseDatabase.getInstance().getReference("usuarios/Naturales");
        this.baseDatos.orderByChild("E").equalTo(sintomas).limitToFirst(limite).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                items.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Usuario usuario = dataSnapshot.getValue(Usuario.class);
                    //No se tiene en cuenta a los usuarios a los cuales ya se les solicito el examen
                    if (!usuario.X.equals("- S"))
                        items.add(new Item(1, usuario, dataSnapshot.getKey(), txtFechaCita.getText().toString()));
                }
                actualizarItems();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("ERROR");
            }
        });
    }

    private void actualizarItems() {
        if (items.size() == 0) {
            items.add(new Item(0, 1, ReporteNotificarCita.this));
            items.add(new Item(0, 1, ReporteNotificarCita.this));
        }
        adaptador.notifyDataSetChanged();
    }

}