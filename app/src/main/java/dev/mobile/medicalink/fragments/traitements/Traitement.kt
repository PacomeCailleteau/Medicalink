package dev.mobile.medicalink.fragments.traitements

import android.os.Build
import androidx.annotation.RequiresApi
import java.io.Serializable
import java.time.LocalDate
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
class Traitement(
    var nomTraitement: String,
    var dosageNb: Int,
    var dosageUnite: String,
    var dateFinTraitement: LocalDate?,
    var typeComprime: String = "Comprimé",
    var comprimesRestants: Int?,
    var expire: Boolean = false,
    var effetsSecondaires: MutableList<String>?,
    var prises: MutableList<Prise>? = null,
    var totalQuantite: Int?,
    var UUID: String?,
    var UUIDUSER: String?,
    var dateDbtTraitement: LocalDate?

) : Serializable {

    fun enMajuscule() {
        nomTraitement = nomTraitement.uppercase(Locale.getDefault())
    }

    fun getName(): String {
        return nomTraitement
    }


    init {
        /*
        if (dateFinTraitement != null) {
            expire = LocalDate.now().plusDays(1) > dateFinTraitement
        }
        */
    }
}