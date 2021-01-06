package com.example.secoco.usuarios.erc_covid.recyclerView;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.secoco.R;
import com.example.secoco.entities.Usuario;
import com.example.secoco.general.Email;
import com.example.secoco.usuarios.erc_covid.ReporteNotificarCita;

import java.util.ArrayList;

public class AdaptadorRecyclerNotificarCita extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<Item> items;

    public AdaptadorRecyclerNotificarCita(ArrayList<Item> items) {
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
            holderUsuario.setUsuario(items.get(position).getUsuario(), items.get(position).getFecha(),
                    items.get(position).getUsuarioKey());
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
        private Usuario usuario;
        private String fecha, usuarioKey;

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
                    Email.enviarCorreoForeGround(view, new String[]{usuario.M}, "Citación para Prueba COVID-19",
                            Email.mensajePersonalizado(view,
                                    new String[]{usuario.N, usuario.I, fecha, usuario.E, usuario.D}));
                    /*Email.enviarCorreoBackGround(view,
                            new String[]{"pedroppax@gmail.com", "Pruebasecoco"},
                            new String[] {usuario.M, "Citación para Prueba COVID-19", mensajePersonalizado(fecha),
                                    "Actualizar Examen", "usuarios/Naturales/" + usuarioKey + "/X", "- S"});*/
                }
            });
        }

        public void setUsuario(Usuario usuario, String fecha, String usuarioKey) {
            this.usuario = usuario;
            this.lblNombre.setText(usuario.N);
            this.lblCorreo.setText(usuario.M);
            this.lblDocumento.setText(usuario.I);
            this.lblLocalidad.setText(usuario.Z);
            this.fecha = fecha;
            this.usuarioKey = usuarioKey;
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
