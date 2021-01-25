package com.example.secoco.usuarios.erc_covid.recyclerView;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.example.secoco.usuarios.erc_covid.ReporteNotificarCita;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AdaptadorRecyclerNotificarCita extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected ArrayList<Item> items;
    protected ReporteNotificarCita reporteNotificarCita;

    public AdaptadorRecyclerNotificarCita(ReporteNotificarCita reporteNotificarCita, ArrayList<Item> items) {
        this.reporteNotificarCita = reporteNotificarCita;
        this.items = items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Item no encontrado
        if (viewType == 0) {
            View view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.item_list_reporte_mensaje, parent, false);
            return new ViewHolderNoEncotrado(view);
        } else {
            //Item Usuario
            View view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.item_list_reporte_cita, parent, false);
            return new ViewHolderCita(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == 0) {
            ViewHolderNoEncotrado holderNoEncotrado = (ViewHolderNoEncotrado) holder;
            holderNoEncotrado.setMensaje(items.get(position).getTipoMensaje(), items.get(position).getReporteNotificarCita());
        } else {
            ViewHolderCita holderUsuario = (ViewHolderCita) holder;
            holderUsuario.setUsuario(items.get(position).getUsuario(), items.get(position).getFecha());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getTipo();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolderCita extends RecyclerView.ViewHolder {

        private TextView lblNombre, lblCorreo, lblDocumento, lblLocalidad;
        private Button btnEnviarCorreo;
        private JSONObject usuario;
        private String fecha;

        public ViewHolderCita(@NonNull View itemView) {
            super(itemView);
            //Inicialización de Atributos
            this.lblNombre = (TextView) itemView.findViewById(R.id.txt_nombre_item_list);
            this.lblCorreo = (TextView) itemView.findViewById(R.id.txt_correo_item_list);
            this.lblDocumento = (TextView) itemView.findViewById(R.id.txt_documento_item_list);
            this.lblLocalidad = (TextView) itemView.findViewById(R.id.txt_localidad_item_list);
            this.btnEnviarCorreo = (Button) itemView.findViewById(R.id.btn_enviar_correo);

            //Accion de Botones
            btnEnviarCorreo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    JSONObject request = new JSONObject();
                    try {
                        request.put("U", usuario.getString("U"));
                        request.put("F", fecha);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                            "https://secocobackend.glitch.me/ENVIAR-CORREO-USUARIO",
                            request,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        boolean puedoEnviar = response.getInt("E") == 1;
                                        if (puedoEnviar) {
                                            Toast.makeText(view.getContext(), "El correo han sido enviado"
                                                    , Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(view.getContext(), "Error al enviar el correo"
                                                    , Toast.LENGTH_SHORT).show();
                                        }
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

        public void setUsuario(JSONObject usuario, String fecha) {
            this.usuario = usuario;
            try {
                this.lblNombre.setText(usuario.getString("N"));
                this.lblCorreo.setText(usuario.getString("M"));
                this.lblDocumento.setText(usuario.getString("I"));
                this.lblLocalidad.setText(usuario.getString("Z"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            this.fecha = fecha;
        }

    }

    public class ViewHolderNoEncotrado extends RecyclerView.ViewHolder {

        private TextView lblMensaje;
        private ReporteNotificarCita reporteNotificarCita;

        public ViewHolderNoEncotrado(@NonNull View itemView) {
            super(itemView);
            this.lblMensaje = (TextView) itemView.findViewById(R.id.lbl_mensaje);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reporteNotificarCita.visibilidadTarjetaFiltro(true);
                }
            });

        }

        @SuppressLint("ResourceType")
        public void setMensaje(int tipoMensaje, ReporteNotificarCita reporteNotificarCita) {
            if (tipoMensaje == 0) {
                lblMensaje.setText(R.string.reporteNotificacion_lbl_mensaje_buscar);
            } else if (tipoMensaje == 1) {
                lblMensaje.setText(R.string.reporteNotificacion_lbl_mensaje_no_encotrado);
            }
            this.reporteNotificarCita = reporteNotificarCita;
        }
    }

}
