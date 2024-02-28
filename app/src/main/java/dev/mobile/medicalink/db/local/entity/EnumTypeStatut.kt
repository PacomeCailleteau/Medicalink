package dev.mobile.medicalink.db.local.entity

import android.content.Context
import dev.mobile.medicalink.R

enum class EnumTypeStatut {
    Medicament,
    Intervalle,
    Spontanee;

    companion object {
        fun getStringFromEnum(type: EnumTypeStatut, context: Context): String {
            return when (type) {
                Medicament -> context.getString(R.string.medicament)
                Intervalle -> context.getString(R.string.dialog_intervalle_regulier)
                Spontanee -> context.getString(R.string.mois)
            }
        }
    }
}