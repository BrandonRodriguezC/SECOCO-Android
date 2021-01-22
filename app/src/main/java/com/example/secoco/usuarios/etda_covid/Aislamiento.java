package com.example.secoco.usuarios.etda_covid;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.secoco.R;
import com.example.secoco.usuarios.etda_covid.recyclerViewAis.AdapterAis;
import com.example.secoco.usuarios.etda_covid.recyclerViewAis.ItemAis;
import java.util.ArrayList;

public class Aislamiento extends AppCompatActivity implements View.OnClickListener{

    private RecyclerView recyclerView;
    private AdapterAis adaptador;
    private ArrayList<ItemAis> itemAis;
    private Spinner spinner;
    private String loc;
    private Button btnEnviar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Reporte aislamiento");
        setContentView(R.layout.activity_aislamiento);

        this.spinner = (Spinner) findViewById(R.id.spiner_localidad);
        this.loc =spinner.getSelectedItem().toString();
        this.btnEnviar = (Button) findViewById(R.id.boton_ais);

        this.btnEnviar.setOnClickListener(this);


        this.recyclerView = (RecyclerView) findViewById(R.id.recycler_aisla);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));


    }


    @Override
    public void onClick(View view) {
       if(view.getId() == btnEnviar.getId()){
           //Esto no esta haciendo nada
           itemAis = new ArrayList<ItemAis>();
           itemAis.add(new ItemAis(loc,Aislamiento.this));
           this.adaptador = new AdapterAis(itemAis);
           this.recyclerView.setAdapter(adaptador);
        }
    }

    private void actualizarItems() {
        itemAis.add(new ItemAis(loc, Aislamiento.this));
        adaptador.notifyDataSetChanged();
    }
}
