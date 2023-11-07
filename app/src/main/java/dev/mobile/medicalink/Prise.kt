package dev.mobile.medicalink

import android.os.Build
import androidx.annotation.RequiresApi
import java.io.Serializable
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
class Prise(var numeroPrise : Int, var heurePrise: String, var quantite : Int, var dosageUnite : String) : Serializable{

    fun enMajuscule() {
        heurePrise = heurePrise.uppercase(Locale.getDefault())
    }

    fun getName(): String {
        return heurePrise
    }


    init {

    }
}