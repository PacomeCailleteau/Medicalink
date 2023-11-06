package dev.mobile.medicalink.utils


import android.app.IntentService
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dev.mobile.medicalink.R

class NotificationService : IntentService("NotificationService") {

    override fun onHandleIntent(intent: Intent?) {
        // Code pour créer et afficher la notification
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.cloche)
            .setContentTitle("Titre de la notification")
            .setContentText("Contenu de la notification")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(notificationId, notification)
    }

    companion object {
        private const val CHANNEL_ID = "YourChannelId"
        private const val notificationId = 123 // ID de la notification (changez-le si nécessaire)
    }
}
