package dev.mobile.medicalink.fragments.traitements

import android.os.Build
import androidx.annotation.RequiresApi
import java.io.Serializable
import java.util.*


class Prise(
    var numeroPrise: String,
    var heurePrise: String,
    var quantite: Int,
    var typeComprime: String
) : Serializable {

    fun enMajuscule() {
        heurePrise = heurePrise.uppercase(Locale.getDefault())
    }

    fun getName(): String {
        return heurePrise
    }

    override fun toString(): String {
        return "$numeroPrise;$heurePrise;$quantite;$typeComprime"
    }

    init {

    }
}
