package com.example.secoco.usuarios.etda_covid.recyclerViewAis;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.secoco.R;
import com.example.secoco.general.RequestAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Intent.getIntent;

public class AdapterAis extends RecyclerView.Adapter<AdapterAis.ViewHolderDatos> {


    ArrayList<ItemAis> datos;

    //constructor
    public AdapterAis(ArrayList<ItemAis> itemAis) {
        this.datos = datos;
    }

    @NonNull
    @Override
    public ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_aislamiento, null, false);
        return new ViewHolderDatos(view);
        //return new ViewHolderAis(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderDatos holder, int position) {
        ViewHolderDatos asignarDatos = (ViewHolderDatos)holder;
        holder.setUsuario(datos.get(position).getUsuario(), datos.get(position).getLocalidad());
    }

    @Override
    public int getItemCount() {
        return datos.size();
    }

    public class ViewHolderDatos extends RecyclerView.ViewHolder {
        private TextView usr_ais,nombre_ais, correo_ais, cedula_ais, fecha_ais;
        private Button boton_ais;
        private JSONObject usuario_ais;
        private Spinner localidad_ais;
        private String locali;

        public ViewHolderDatos(@NonNull View itemView) {
            super(itemView);
            //Inicialización de Atributos

            //usuario_ais=(JSONObject) itemView.findViewById(R.id.item_usuario_ais).toString();
            this.usr_ais = (TextView) itemView.findViewById(R.id.item_usuario_ais);
            this.nombre_ais = (TextView) itemView.findViewById(R.id.item_nombre_ais);
            this.correo_ais = (TextView) itemView.findViewById(R.id.item_correo_ais);
            this.cedula_ais = (TextView) itemView.findViewById(R.id.item_cedula_ais);
            this.localidad_ais = (Spinner) itemView.findViewById(R.id.spiner_localidad);
            this.locali =localidad_ais.getSelectedItem().toString();
            //Accion de Botones
            boton_ais.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    JSONObject request = new JSONObject();

                    try {
                        request.put("Z", localidad_ais);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                            "https://secocobackend.glitch.me/MOVIMIENTO-EN-AISLAMIENTO",
                            request,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        String  puedoEnviar = response.getString("Z");
                                            Toast.makeText(view.getContext(), "El correo han sido enviado"
                                                    , Toast.LENGTH_SHORT).show();

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(view.getContext(), "Error de Conexión"
                                            , Toast.LENGTH_SHORT).show();
                                }
                            });
                    jsonObjectRequest.setShouldCache(false);
                    RequestAPI.getInstance(view.getContext()).add(jsonObjectRequest);

                }
            });
        }

            public void setUsuario(JSONObject usuario,String localidad) {
                this.usuario_ais=usuario;
                try {
                    this.usr_ais.setText(usuario.getString("USUARIO"));
                    this.nombre_ais.setText(usuario.getString("N"));
                    this.correo_ais.setText(usuario.getString("M"));
                    this.cedula_ais.setText(usuario.getString("I"));
                    this.fecha_ais.setText(usuario.getString("F"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                this.locali = localidad;
            }

        }
        }








