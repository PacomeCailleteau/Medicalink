package dev.mobile.medicalink.utils


import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import dev.mobile.medicalink.R
import dev.mobile.medicalink.fragments.traitements.Traitement
import java.lang.System.currentTimeMillis
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime


class NotificationService : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
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
        /**
         * Fonction qui créer la première notification d'un traitement
         * @param context : le contexte de l'application
         * @param heurePremierePriseStr : l'heure de la première prise
         * @param jourPremierePrise : le jour de la première prise
         * @param traitement : le traitement concerné
         */
        @RequiresApi(Build.VERSION_CODES.O)
        fun createFirstNotif(context: Context, heurePremierePriseStr : String, jourPremierePrise : LocalDate, traitement : Traitement){
            //On découpe le string pour récupérer l'heure et les minutes
            val heure = heurePremierePriseStr.split(":").first().toInt()
            val minute = heurePremierePriseStr.split(":").last().toInt()

            //On récupère l'heure actuelle en millisecondes
            val heureActuelle = LocalTime.now().toNanoOfDay() / 1000000

            //On récupère l'heure de la prochaine prise en millisecondes
            val heureProchainePriseMillis = LocalTime.of(heure, minute).toNanoOfDay() / 1000000

            //On calcule le nombre de jour entre ajourd'hui et le jour de la première prise
            var nbJours = Duration.between(LocalDate.now().atStartOfDay(), jourPremierePrise.atStartOfDay()).toDays().toInt()

            //On rajoute un jour si l'heure de la première prise est inférieure à l'heure actuelle
            if (heureProchainePriseMillis < heureActuelle) {
                nbJours++
            }
            createNotif(context, heurePremierePriseStr, traitement, nbJours)
        }

        /**
         * Fonction qui créer une notification pour la prochaine prise du traitement
         * @param context : le contexte de l'application
         * @param heureProchainePriseStr : l'heure de la prochaine prise
         * @param traitement : le traitement concerné
         */
        @RequiresApi(Build.VERSION_CODES.O)
        fun createNextNotif(context: Context, heureProchainePriseStr : String, traitement : Traitement){
            createNotif(context, heureProchainePriseStr, traitement, 1)
        }

        /**
         * Fonction qui créer une notification pour une prise future du traitement
         * @param context : le contexte de l'application
         * @param heurePriseStr : l'heure de la prise
         * @param traitement : le traitement concerné
         * @param nbJour : le nombre de jour entre aujourd'hui et le jour de la prise
         */
        @RequiresApi(Build.VERSION_CODES.O)
        private fun createNotif(context: Context, heurePriseStr: String, traitement: Traitement, nbJour: Int){
            //On découpe le string pour récupérer l'heure et les minutes
            val heure = heurePriseStr.split(":").first().toInt()
            val minute = heurePriseStr.split(":").last().toInt()

            //On récupère l'heure actuelle en millisecondes
            val heureActuelle = LocalTime.now().toNanoOfDay() / 1000000

            //On récupère l'heure de la prochaine prise en millisecondes
            val heureProchainePriseMillis = LocalTime.of(heure, minute).toNanoOfDay() / 1000000

            //On récupère la durée entre l'heure actuelle et l'heure de la prochaine prise
            //Il faut faire attention à la date, si l'heure de la prochaine prise est inférieure à l'heure actuelle, on ajoute un jour à la date
            val duree = if (heureProchainePriseMillis < heureActuelle) {
                Duration.ofMillis(heureProchainePriseMillis + 86400000*nbJour - heureActuelle)
            } else {
                Duration.ofMillis(heureProchainePriseMillis - heureActuelle)
            }

            // On créer la notification
            sendNotification(
                context,
                "Prise de ${traitement.nomTraitement}",
                "Il est l'heure de prendre votre médicament ${traitement.nomTraitement}",
                duree.toMillis()
            )
        }

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

        fun sendEveryXHoursNotification(
            context: Context,
            titre: String,
            contenu: String,
            delayMillis: Long,
            interval: Int
        ) {
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

        fun sendEveryXDaysNotification(
            context: Context,
            titre: String,
            contenu: String,
            delayMillis: Long,
            interval: Int = 1
        ) {
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

        fun sendEveryXWeeksNotification(
            context: Context,
            titre: String,
            contenu: String,
            delayMillis: Long,
            interval: Int = 1
        ) {
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

        fun sendEveryXMonthsNotification(
            context: Context,
            titre: String,
            contenu: String,
            delayMillis: Long,
            interval: Int = 1
        ) {
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



