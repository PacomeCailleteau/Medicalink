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
import dev.mobile.medicalink.MainActivity
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
        val notificationId = intent?.getIntExtra("notificationId", -1)!!

        // C'est ici que vous affichez la notification
        showNotification(context, title, content, notificationId)
    }

    private fun showNotification(context: Context?, titre: String, contenu: String, notificationId: Int) {
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

        // utiliser l'id si on veut avoir plusieurs notifications en même temps
        // Sinon la nouvelle notif remplacera la première
        notificationManager.notify(notificationId, notificationBuilder.build())
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
        fun createFirstNotif(
            context: Context,
            heurePremierePriseStr: String,
            jourPremierePrise: LocalDate,
            traitement: Traitement
        ) {
            //On découpe le string pour récupérer l'heure et les minutes
            val heure = heurePremierePriseStr.split(":").first().toInt()
            val minute = heurePremierePriseStr.split(":").last().toInt()

            //On récupère l'heure actuelle en millisecondes
            val heureActuelle = LocalTime.now().toNanoOfDay() / 1000000

            //On récupère l'heure de la prochaine prise en millisecondes
            val heureProchainePriseMillis = LocalTime.of(heure, minute).toNanoOfDay() / 1000000

            //On calcule le nombre de jour entre ajourd'hui et le jour de la première prise
            var nbJours =
                Duration.between(LocalDate.now().atStartOfDay(), jourPremierePrise.atStartOfDay())
                    .toDays().toInt()

            //On rajoute un jour si l'heure de la première prise est inférieure à l'heure actuelle
            if (heureProchainePriseMillis < heureActuelle) {
                nbJours++
            }

            // Appelez createNotif avec le PendingIntent nouvellement créé
            createNotif(
                context,
                heurePremierePriseStr,
                traitement,
                nbJours
            )
        }

        /**
         * Fonction qui créer une notification pour la prochaine prise du traitement
         * @param context : le contexte de l'application
         * @param heureProchainePriseStr : l'heure de la prochaine prise
         * @param traitement : le traitement concerné
         */
        @RequiresApi(Build.VERSION_CODES.O)
        fun createNextNotif(
            context: Context,
            heureProchainePriseStr: String,
            traitement: Traitement
        ) {
            // Appelez createNotif avec le PendingIntent nouvellement créé
            createNotif(
                context,
                heureProchainePriseStr,
                traitement,
                1
            )
        }

        /**
         * Fonction qui créer une notification pour une prise future du traitement
         * @param context : le contexte de l'application
         * @param heurePriseStr : l'heure de la prise
         * @param traitement : le traitement concerné
         * @param nbJour : le nombre de jour entre aujourd'hui et le jour de la prise
         */
        @RequiresApi(Build.VERSION_CODES.O)
        private fun createNotif(
            context: Context,
            heurePriseStr: String,
            traitement: Traitement,
            nbJour: Int
        ) : Int {
            val notificationId = uniqueId(context)

            // On découpe le string pour récupérer l'heure et les minutes
            val heure = heurePriseStr.split(":").first().toInt()
            val minute = heurePriseStr.split(":").last().toInt()

            // On récupère l'heure actuelle en millisecondes
            val heureActuelle = LocalTime.now().toNanoOfDay() / 1000000

            // On récupère l'heure de la prochaine prise en millisecondes
            val heureProchainePriseMillis =
                LocalTime.of(heure, minute).toNanoOfDay() / 1000000

            // On récupère la durée entre l'heure actuelle et l'heure de la prochaine prise
            // Il faut faire attention à la date, si l'heure de la prochaine prise est inférieure à l'heure actuelle, on ajoute un jour à la date
            val duree = if (heureProchainePriseMillis < heureActuelle) {
                Duration.ofMillis(heureProchainePriseMillis + 86400000 * nbJour - heureActuelle)
            } else {
                Duration.ofMillis(heureProchainePriseMillis - heureActuelle)
            }

            // Intent pour l'action "Sauter"
            val sauterIntent = Intent(context, SauterReceiver::class.java)
            sauterIntent.action = "ACTION_SAUTE"
            sauterIntent.putExtra("notificationId", notificationId)
            val sauterPendingIntent = PendingIntent.getBroadcast(
                context,
                notificationId,
                sauterIntent,
                PendingIntent.FLAG_IMMUTABLE
            )

            // Intent pour l'action "Prendre"
            val prendreIntent = Intent(context, PrendreReceiver::class.java)
            prendreIntent.action = "ACTION_PRENDRE"
            prendreIntent.putExtra("notificationId", notificationId)
            val prendrePendingIntent = PendingIntent.getBroadcast(
                context,
                notificationId+1,
                prendreIntent,
                PendingIntent.FLAG_IMMUTABLE
            )

            // On crée la notification en utilisant l'ID généré
            return sendNotification(
                context,
                "Prise de ${traitement.nomTraitement}",
                "Il est l'heure de prendre votre médicament ${traitement.nomTraitement}",
                duree.toMillis(),
                notificationId,
                sauterPendingIntent,
                prendrePendingIntent
            )
        }


        /**
         * Fonction qui créer une notification
         * @param context : le contexte de l'application
         * @param titre : le titre de la notification
         * @param contenu : le contenu de la notification
         * @param delayMillis : le délai avant l'affichage de la notification
         * @param notificationId : l'id de la notification
         * @param sauterPendingIntent : le pending intent pour l'action "Sauter"
         * @param prendrePendingIntent : le pending intent pour l'action "Prendre"
         * @return l'id de la notification
         */
        fun sendNotification(
            context: Context,
            titre: String,
            contenu: String,
            delayMillis: Long,
            notificationId: Int? = null,
            sauterPendingIntent: PendingIntent? = null,
            prendrePendingIntent: PendingIntent? = null
        ): Int {
            val notificationManager = ContextCompat.getSystemService(
                context,
                NotificationManager::class.java
            ) as NotificationManager

            val channelId = "medicalinkNotificationChannel"

            // Si l'un des paramètres est null, on fait une notification sans action
            if (notificationId == null || sauterPendingIntent == null || prendrePendingIntent == null) {
                val notificationBuilder = NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.logo_medicalink)
                    .setContentTitle(titre)
                    .setContentText(contenu)
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                val id = uniqueId(context)
                notificationManager.notify(id, notificationBuilder.build())

                return id
            }

            val notificationBuilder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.logo_medicalink)
                .setContentTitle(titre)
                .setContentText(contenu)
                .setAutoCancel(true)  // La notification sera supprimée lorsque l'utilisateur cliquera dessus
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .addAction(0, "Sauter", sauterPendingIntent)
                .addAction(0, "Prendre", prendrePendingIntent)

            // Créez le PendingIntent pour l'ouverture de l'application
            val openAppIntent = Intent(context, MainActivity::class.java)
            openAppIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                openAppIntent,
                PendingIntent.FLAG_IMMUTABLE
            )

            notificationBuilder.setContentIntent(pendingIntent)

            notificationManager.notify(notificationId, notificationBuilder.build())

            return notificationId
        }


        fun uniqueId(context: Context): Int {
            val PREFS_NAME = "notification_prefs"
            val KEY_NOTIFICATION_ID = "notification_id"
            val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            // Get the current notification ID
            val notificationId = sharedPref.getInt(KEY_NOTIFICATION_ID, 0)

            // Increment the notification ID and store it
            with(sharedPref.edit()) {
                putInt(KEY_NOTIFICATION_ID, notificationId + 2)
                apply()
            }

            return notificationId + 2
        }


    }

}



