package dev.mobile.medicalink.utils

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat

class SauterReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // Traitement pour l'action "Sauter"
        // Mettez en œuvre la logique pour indiquer que la prise a été passée dans l'application
        // Par exemple, utilisez une préférence partagée ou une base de données pour stocker l'information

        // Fermez la notification
        val notificationManager =
            context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Annulez la notification avec l'ID spécifié dans l'intent
        val notificationId = intent!!.getIntExtra("notificationId", -1)
        notificationManager?.cancel(notificationId)

        Log.d("SauterReceiver", "Notification annulée avec succès. ID: $notificationId")
    }
}
