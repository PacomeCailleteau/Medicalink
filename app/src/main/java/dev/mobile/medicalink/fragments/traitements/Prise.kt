package dev.mobile.medicalink.fragments.traitements

import java.io.Serializable
import java.util.Locale


class Prise(
    var numeroPrise: String,
    var heurePrise: String,
    var quantite: Int,
    var typeComprime: String
) : Serializable {

    fun enMajuscule() {
        heurePrise = heurePrise.uppercase(Locale.getDefault())
    }

    override fun toString(): String {
        return "$numeroPrise;$heurePrise;$quantite;$typeComprime"
    }
}
