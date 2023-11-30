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
import java.lang.System.currentTimeMillis


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

        val channelId = "medicalinkNotificationChannel" // Remplacez par votre propre ID de canal
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.logo_medicalink)
            .setContentTitle(titre)
            .setContentText(contenu)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // Affichez la notification
        val id = currentTimeMillis().toInt()
        // utiliser l'id si on veut avoir plusieurs notifications en même temps
        // Sinon la nouvelle notif remplacera la première
        notificationManager.notify(1, notificationBuilder.build())
    }


    companion object {
        // Fonction qui créer une notification avec un délai en millisecondes
        fun sendNotification(context: Context, titre: String, contenu: String, delayMillis: Long) {
            val notificationIntent = Intent(context, NotificationService::class.java)
                .putExtra("title", titre)
                .putExtra("content", contenu)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                uniqueId(),
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            // Configurez l'alarme avec le délai
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                currentTimeMillis() + delayMillis,
                pendingIntent
            )
        }

        fun sendEveryXHoursNotification(context: Context, titre: String, contenu: String, delayMillis: Long, interval: Int) {
            val notificationIntent = Intent(context, NotificationService::class.java)
                .putExtra("title", titre)
                .putExtra("content", contenu)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                uniqueId(),
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            // Configurez l'alarme avec le délai
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                currentTimeMillis() + delayMillis,
                AlarmManager.INTERVAL_HOUR * interval,
                pendingIntent
            )
        }

        fun sendEveryXDaysNotification(context: Context, titre: String, contenu: String, delayMillis: Long, interval: Int = 1) {
            val notificationIntent = Intent(context, NotificationService::class.java)
                .putExtra("title", titre)
                .putExtra("content", contenu)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                uniqueId(),
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            // Configurez l'alarme avec le délai
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                currentTimeMillis() + delayMillis,
                AlarmManager.INTERVAL_DAY * interval,
                pendingIntent
            )
        }

        fun sendEveryXWeeksNotification(context: Context, titre: String, contenu: String, delayMillis: Long, interval: Int = 1) {
            val notificationIntent = Intent(context, NotificationService::class.java)
                .putExtra("title", titre)
                .putExtra("content", contenu)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                uniqueId(),
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            // Configurez l'alarme avec le délai
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                currentTimeMillis() + delayMillis,
                AlarmManager.INTERVAL_DAY * 7 * interval,
                pendingIntent
            )
        }

        fun sendEveryXMonthsNotification(context: Context, titre: String, contenu: String, delayMillis: Long, interval: Int = 1) {
            val notificationIntent = Intent(context, NotificationService::class.java)
                .putExtra("title", titre)
                .putExtra("content", contenu)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                uniqueId(),
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            // Configurez l'alarme avec le délai
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                currentTimeMillis() + delayMillis,
                AlarmManager.INTERVAL_DAY * 30 * interval,
                pendingIntent
            )
        }



        private fun uniqueId(): Int {
            return currentTimeMillis().toInt()
        }
    }

}



