package dev.mobile.medicalink.utils


import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import dev.mobile.medicalink.R


class NotificationService : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // C'est ici que vous affichez la notification
        showNotification(context)
    }

    private fun showNotification(context: Context?) {
        // Code pour afficher la notification
        val notificationManager = ContextCompat.getSystemService(
            context!!,
            NotificationManager::class.java
        ) as NotificationManager

        val channelId = "soleil123" // Remplacez par votre propre ID de canal
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.cloche)
            .setContentTitle("Titre de la notification")
            .setContentText("Contenu de la notification")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // Affichez la notification
        notificationManager.notify(1, notificationBuilder.build())
    }

}

