package com.example.secoco.general;

import android.app.Activity;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Query {

    /* orderByChild sirve para seleccionar porque key hacer la busqueda
     * endAt sirve para valores menores o iguales al parametro
     * equalsTo sirve para valores iguales
     * startAt sirve para valores mayores o iguales al parametro */
    private static DatabaseReference baseDatos;

    /* Metodo utilizado para cambiar cualquier valor dentro de la base de datos */
    public static void actualizarDatoUsuario(Activity actividad, String ruta, String valor, String[] mensajes) {
        baseDatos = FirebaseDatabase.getInstance().getReference(ruta);
        baseDatos.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                baseDatos.setValue(valor);
                if (!mensajes[0].equals(""))
                    Toast.makeText(actividad, mensajes[0], Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (!mensajes[1].equals(""))
                    Toast.makeText(actividad, mensajes[1], Toast.LENGTH_SHORT).show();
            }
        });
    }

}
