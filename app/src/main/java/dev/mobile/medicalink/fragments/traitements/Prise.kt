package dev.mobile.medicalink.fragments.traitements

import dev.mobile.medicalink.fragments.traitements.enums.EnumTypeMedic
import java.io.Serializable


class Prise(
    var numeroPrise: String,
    var heurePrise: String,
    var quantite: Int,
    var typeComprime: EnumTypeMedic
) : Serializable {
    override fun toString(): String {
        return "$numeroPrise;$heurePrise;$quantite;$typeComprime"
    }
}
