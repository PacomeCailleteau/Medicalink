package dev.mobile.medicalink.utils


import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import dev.mobile.medicalink.R


class NotificationService : BroadcastReceiver() {
    override fun onReceive(context: Context?,  intent: Intent?) {
        // Obtenez le titre et le contenu de l'intent
        val title = intent?.getStringExtra("title") ?: "Titre par défaut"
        val content = intent?.getStringExtra("content") ?: "Contenu par défaut"

        // C'est ici que vous affichez la notification
        showNotification(context, title, content)
    }

    private fun showNotification(context: Context?, titre: String, contenu: String) {
        // Code pour afficher la notification
        val notificationManager = ContextCompat.getSystemService(
            context!!,
            NotificationManager::class.java
        ) as NotificationManager

        val channelId = "soleil123" // Remplacez par votre propre ID de canal
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.logo_medicalink)
            .setContentTitle(titre)
            .setContentText(contenu)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // Affichez la notification
        notificationManager.notify(1, notificationBuilder.build())
    }

}

