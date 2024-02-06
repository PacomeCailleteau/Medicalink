package dev.mobile.medicalink.utils.notification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dev.mobile.medicalink.db.local.AppDatabase
import dev.mobile.medicalink.db.local.entity.PriseValidee
import dev.mobile.medicalink.db.local.repository.PriseValideeRepository
import java.util.UUID

/**
 * Classe permettant de gérer la réception d'une notification de prise de médicament avec l'action "prendre"
 */
class PrendreReceiver : BroadcastReceiver() {

    /**
     * Méthode appelée lors de la réception d'une notification de prise de médicament avec l'action "prendre"
     * @param context
     * @param intent
     */
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("PrendreReceiver", "avant test null")
        // s'il nous manque une info alors on arrête
        if (context == null || intent == null) return
        Log.d("PrendreReceiver", "après test null")

        // On ferme la notification
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationId = intent.getIntExtra("notificationId", -1)
        notificationManager.cancel(notificationId)

        //On récupère les infos dont on a besoin pour créer une prise validée
        val date = intent.getStringExtra("date") ?: ""
        val numero = intent.getStringExtra("numero") ?: ""
        val db = AppDatabase.getInstance(context)
        val priseValideeDatabaseInterface = PriseValideeRepository(db.priseValideeDao())
        Thread {
            val priseToUpdate = priseValideeDatabaseInterface.getByUUIDTraitementAndDate(
                date,
                numero
            )
            if (priseToUpdate.isNotEmpty()) {
                val maPrise = priseToUpdate.first()
                maPrise.statut = "prendre"
                priseValideeDatabaseInterface.updatePriseValidee(maPrise)
            } else {
                val priseValidee = PriseValidee(
                    uuid = UUID.randomUUID().toString(),
                    date = date,
                    uuidPrise = numero,
                    statut = "prendre",
                )
                priseValideeDatabaseInterface.insertPriseValidee(priseValidee)
            }
        }.start()

        //TODO : lancer la prochaine notification

    }
}
