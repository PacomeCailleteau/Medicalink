package dev.mobile.medicalink.utils.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Classe permettant de gérer la réception d'une notification de prise de médicament avec l'action "sauter"
 */
class SauterReceiver : BroadcastReceiver() {

    /**
     * Méthode appelée lors de la réception d'une notification de prise de médicament avec l'action "prendre"
     * @param context
     * @param intent
     */
    override fun onReceive(context: Context?, intent: Intent?) {
        // s'il nous manque une info alors on arrête
        if (context == null || intent == null) return
        NotificationService.gererNotif(context, intent, "sauter")
    }
}
