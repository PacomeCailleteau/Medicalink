package dev.mobile.medicalink.utils


import android.app.IntentService
import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dev.mobile.medicalink.R


class NotificationService : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //createNotificationChannel(notificationManager)

        val notification = intent!!.getParcelableExtra<Notification>("notification")

        notificationManager.notify(2, notification)
    }
}
