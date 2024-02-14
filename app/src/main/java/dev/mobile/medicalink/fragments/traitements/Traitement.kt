package dev.mobile.medicalink.fragments.traitements

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import java.io.Serializable
import java.time.LocalDate
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
class Traitement(
    var nomTraitement: String,
    var codeCIS: String,
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

    var suggDuree: String? = null

    fun enMajuscule() {
        nomTraitement = nomTraitement.uppercase(Locale.getDefault())
    }

    fun getName(): String {
        return nomTraitement
    }

    fun getProchainePrise(prise: Prise?): Prise {
        if (prises == null) {
            return Prise("-1", "14:38", 0, "")
        }
        if (prise == null) {
            return prises!![0]
        } else {
            var prochainePrise = prise
            //S'il n'y a qu'une seule prise, on retourne cette prise
            if (prises?.size == 1) {
                return prochainePrise
            }
            //Sinon :
            //On tri les prises en fonction de leur heure de prise
            prises?.sortBy { it.heurePrise }
            //On boucle sur les prises pour trouver la prochaine prise, si la prise est la dernière de la liste, on retourne la première prise
            for (i in 0 until prises!!.size) {
                if (prises!![i] == prise) {
                    prochainePrise = if (i == prises!!.size - 1) {
                        prises!![0]
                    } else {
                        prises!![i + 1]
                    }
                }
            }

            //On est de toute façon dans le else alors prochainePrise ne peut pas être null
            return prochainePrise!!
        }
    }

    private fun trouveNom(context: Context) {
        /*
        Méthode qui va lancer un algorithme pour trouver ce qui se rapproche le plus du nom du médicament
         */
        val algo = JaroWinkler()
        algo.getNomMedic(context)
        algo.aChercher = this.nomTraitement

        this.nomTraitement = algo.lancerDistance()
    }


    private fun trouveUnite() {
        /*
        Méthode qui conformise l'unité du médicament
         */
        TODO("not yet implemented")
    }


    private fun trouveDuree() {
        /*
        Méthode qui va remplir les variables dateDbtTraitement et dateFinTraitement
         */
        TODO("not yet implemented")
    }


    fun paufine() {
        /*
        Méthode qui va réellement créer un traitement à partir des données qu'il possède
        va notament appeler les méthodes privées
         */
        TODO("Not yet Implemented")
    }

}
