package dev.mobile.td3notes

import java.time.LocalDate
import java.util.*

class Traitement(var nomTraitement: String) {

    fun enMajuscule() {
        nomTraitement = nomTraitement.uppercase(Locale.getDefault())
    }

    fun getName(): String {
        return nomTraitement
    }


    init {
    }
}