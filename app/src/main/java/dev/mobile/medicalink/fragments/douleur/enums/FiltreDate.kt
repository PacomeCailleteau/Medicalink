package dev.mobile.medicalink.fragments.douleur.enums

import android.content.Context
import dev.mobile.medicalink.R

enum class FiltreDate {
    JOUR,
    SEMAINE,
    MOIS;

    companion object {
        fun getStringFromEnum(enum: FiltreDate, context: Context): String {
            return when (enum) {
                JOUR -> context.getString(R.string.jour)
                SEMAINE -> context.getString(R.string.semaines)
                MOIS -> context.getString(R.string.mois)
            }
        }
    }
}