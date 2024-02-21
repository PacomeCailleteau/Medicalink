package dev.mobile.medicalink.fragments.traitements

import android.content.Context
import dev.mobile.medicalink.R

enum class EnumFrequence {
    QUOTIDIEN,
    JOUR,
    SEMAINE,
    MOIS,
    AUBESOIN;

    companion object {
        fun getStringFromEnum(enum: EnumFrequence, context: Context): String {
            return when (enum) {
                QUOTIDIEN -> context.getString(R.string.quotidiennement)
                JOUR -> context.getString(R.string.jour)
                SEMAINE -> context.getString(R.string.semaines)
                MOIS -> context.getString(R.string.mois)
                AUBESOIN -> context.getString(R.string.au_besoin)
            }
        }
    }
}