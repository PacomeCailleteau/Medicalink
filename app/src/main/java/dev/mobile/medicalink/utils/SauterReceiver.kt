package dev.mobile.medicalink.utils

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

class SauterReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // Traitement pour l'action "Sauter"
        if (intent?.action == "sauter") {
            // Mettez en œuvre la logique pour indiquer que la prise a été passée dans l'application
            // Par exemple, utilisez une préférence partagée ou une base de données pour stocker l'information

            // Obtenez l'ID de la notification à annuler depuis l'intent
            val notificationId = intent.getIntExtra("notificationId", -1)

            if (notificationId != -1) {
                // Fermez la notification
                val notificationManager =
                    ContextCompat.getSystemService(context!!, NotificationManager::class.java)
                notificationManager?.cancel(notificationId)
            }
            val notificationManager =
                ContextCompat.getSystemService(context!!, NotificationManager::class.java)
            notificationManager?.cancel(notificationId)
        }
    }
}

