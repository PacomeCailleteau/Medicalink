package dev.mobile.medicalink.utils

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

class PrendreReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // Traitement pour l'action "Prendre"
        if (intent?.action == "prendre") {
            // Mettez en œuvre la logique pour indiquer que la prise a été effectuée dans l'application
            // Par exemple, utilisez une préférence partagée ou une base de données pour stocker l'information

            // Fermez la notification
            val notificationManager =
                ContextCompat.getSystemService(context!!, NotificationManager::class.java)
            notificationManager?.cancel(1)
        }
    }
}
