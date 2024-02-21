package dev.mobile.medicalink.fragments.traitements

import java.io.Serializable


class Prise(
    var numeroPrise: String,
    var heurePrise: String,
    var quantite: Int,
    var typeComprime: String
) : Serializable {
    override fun toString(): String {
        return "$numeroPrise;$heurePrise;$quantite;$typeComprime"
    }
}
