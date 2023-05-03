package sv.edu.udb.dsm.medimanager.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AlarmReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        buildNotification(context, "Notificación", "Test de ejecución")
    }

    private fun buildNotification(context: Context?, title: String, message: String) {
        Log.i("MEDIMANAGER", "Notificación $title. Mensaje: $message")

        // TODO: Cambiar lo anterior con el código de la notificación para el usuario.
    }

}