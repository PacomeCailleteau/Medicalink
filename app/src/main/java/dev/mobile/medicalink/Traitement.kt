package dev.mobile.medicalink

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
class Traitement(var nomTraitement: String, var dosageNb : Int, var dosageUnite : String, var dateFinTraitement : LocalDate?, var comprimesRestants : Int, var expire : Boolean = true,var effetsSecondaires : MutableList<String>?) {

    fun enMajuscule() {
        nomTraitement = nomTraitement.uppercase(Locale.getDefault())
    }

    fun getName(): String {
        return nomTraitement
    }


    init {
        if (dateFinTraitement!=null){
            expire = LocalDate.now() > dateFinTraitement
        }
    }
}