package com.example.secoco.general;

import android.app.Activity;
import android.os.AsyncTask;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class JavaMail extends AsyncTask<String, Void, Void> {

    private final Activity activity;

    private Session sesion;
    private final String emailOrigen, constrasenaOrigen;


    public JavaMail(Activity activity, String origen[]) {
        this.activity = activity;
        this.emailOrigen = origen[0];
        this.constrasenaOrigen = origen[1];
    }

    /*Metodo encargado de crear un hilo y enviar el correo electronico al destinatario
    * strings[0] = emailDestino
    * strings[1] = asunto
    * strings[2] = mensaje
    * Dominio de la fuente del correo:
    *   // Gmail -> smtp.gmail.com, hotmail ->  smtp.live.com, outlook -> smtp.Office365.com
    * Puerto por el cual se envian los correos
    *   // Gmail -> 465,  Hotmail -> 25, 465 (con autentificación) outlook -> 587
    * */
    @Override
    protected Void doInBackground(String... strings) {
        String emailDestino = strings[0];
        String asunto = strings[1];
        String mensaje = strings[2];

        Properties properties = new Properties();

        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.socketFactory.port", "465");
        properties.put("mail.smtp.port", "465");

        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.auth", "true");

        sesion = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailOrigen, constrasenaOrigen);
            }
        });

        MimeMessage mimeMessage = new MimeMessage(sesion);
        try {
            mimeMessage.setFrom(new InternetAddress(emailOrigen));
            mimeMessage.addRecipients(Message.RecipientType.TO, String.valueOf(new InternetAddress(emailDestino)));
            mimeMessage.setSubject(asunto);
            mimeMessage.setText(mensaje);
            Transport.send(mimeMessage);
            // Aqui se debe poner que respuesta se debe agregar dependiendo del activity
            /*activity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(activity, "Hola Esta funcionando", Toast.LENGTH_SHORT).show();
                }
            });*/
            if (strings.length > 3) {
                actualizarInformacion(strings[3], strings[4], strings[5]);
            }
        } catch (MessagingException e) {
            e.getMessage();
        }
        return null;
    }


    /*Metodo encargado de hacer operaciones extra despues de enviar el correo electronico
    * Actualizar Examen -> Se encarga de actualizar el examen del paciente segun los diferentes estados
    * (- A "Activo", - S "Solicitado" y - I "Inactivo") */
    private void actualizarInformacion(String accion, String ruta, String valor) {
        if (accion.equals("Actualizar Examen")) {
            Query.actualizarDatoUsuario(activity, ruta,
                    valor, new String[]{"Actualización Completada a: "+ ruta.split("/")[2], "Error al intentar Actualizar"});
        } else if (accion.equals("")) {

        }
    }


}
