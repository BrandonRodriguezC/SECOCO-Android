package com.example.secoco.usuarios.etda_covid.recyclerViewAis;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.secoco.R;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

public class AdapterAis extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    ArrayList<ItemAis> datos;

    //Esta bien
    public AdapterAis(ArrayList<ItemAis> datos) {
        this.datos = datos;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_aislamiento, parent, false);
        return new ViewHolderDatos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolderDatos asignarDatos = (ViewHolderDatos) holder;
        asignarDatos.setLocali(datos.get(position).getLocalidad());
    }

    //Esta bien
    @Override
    public int getItemCount() {
        return datos.size();
    }

    public class ViewHolderDatos extends RecyclerView.ViewHolder {
        private TextView usr_ais, nombre_ais, correo_ais, cedula_ais, fecha_ais;
        private JSONObject localidad;

        public ViewHolderDatos(@NonNull View itemView) {
            super(itemView);
            this.nombre_ais = (TextView) itemView.findViewById(R.id.item_nombre_ais);
            this.correo_ais = (TextView) itemView.findViewById(R.id.item_correo_ais);
            this.cedula_ais = (TextView) itemView.findViewById(R.id.item_cedula_ais);
            this.usr_ais = (TextView) itemView.findViewById(R.id.item_usuario_ais);
            this.fecha_ais = (TextView) itemView.findViewById(R.id.item_fecha_ais);

        }

        public void setLocali(JSONObject localidad) {
            this.localidad = localidad;
            try {
                this.nombre_ais.setText(localidad.getString("nombre"));
                this.correo_ais.setText(localidad.getString("correo"));
                this.cedula_ais.setText(localidad.getString("cedula"));
                this.usr_ais.setText(localidad.getString("usuario"));
                this.fecha_ais.setText(localidad.getString("fecha"));

            } catch (JSONException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

}








