package dev.mobile.medicalink.fragments.traitements

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.util.*
import java.io.Serializable

@RequiresApi(Build.VERSION_CODES.O)
class Traitement (var nomTraitement: String, var dosageNb : Int, var dosageUnite : String, var dateFinTraitement : LocalDate?, var typeComprime : String = "Comprimé",var comprimesRestants : Int, var expire : Boolean = true,var effetsSecondaires : MutableList<String>?, var prises : MutableList<Prise>? = null) : Serializable{

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