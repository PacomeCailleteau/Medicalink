package dev.mobile.medicalink.fragments.traitements.enums

import android.content.Context
import dev.mobile.medicalink.R

enum class EnumTypeMedic {
    COMPRIME,
    GELLULE,
    SACHET,
    SIROP,
    PIPETTE,
    SERINGUE,
    BONBON;

    companion object {
        fun getStringFromEnum(enum: EnumTypeMedic, context: Context): String {
            return when (enum) {
                COMPRIME -> context.getString(R.string.comprime)
                GELLULE -> context.getString(R.string.gellule)
                SACHET -> context.getString(R.string.sachet)
                SIROP -> context.getString(R.string.sirop)
                PIPETTE -> context.getString(R.string.pipette)
                SERINGUE -> context.getString(R.string.seringue)
                BONBON -> context.getString(R.string.bonbon)
            }
        }
    }
}