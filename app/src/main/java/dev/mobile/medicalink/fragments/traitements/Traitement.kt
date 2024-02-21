package dev.mobile.medicalink.fragments.traitements

import android.content.Context
import dev.mobile.medicalink.fragments.traitements.enums.EnumFrequence
import dev.mobile.medicalink.fragments.traitements.enums.EnumTypeMedic
import java.io.Serializable
import java.time.LocalDate


class Traitement(
    var nomTraitement: String,
    var codeCIS: String,
    var dosageNb: Int,
    var frequencePrise: EnumFrequence,
    var dateFinTraitement: LocalDate?,
    var typeComprime: EnumTypeMedic = EnumTypeMedic.COMPRIME,
    var comprimesRestants: Int?,
    var expire: Boolean = false,
    var effetsSecondaires: MutableList<String>?,
    var prises: MutableList<Prise>? = null,
    var totalQuantite: Int?,
    var uuid: String?,
    var uuidUser: String?,
    var dateDbtTraitement: LocalDate

) : Serializable {

    var suggDuree: String? = null
    var suggFrequence: String? = null

    /**
     * Renvoie la prochaine prise à effectuer
     * @param prise la prise actuelle
     * @return la prochaine prise à effectuer
     */
    fun getProchainePrise(prise: Prise?): Prise {
        return if (prises == null || prises!!.isEmpty()) {
            return Prise("-1", "14:38", 0, EnumTypeMedic.COMPRIME)
        } else if (prise == null) {
            prises!![0]
        } else {
            return calculProchainPrise(prise)
        }
    }

    /**
     * Calcul la prochaine prise à effectuer
     * @param prise la prise actuelle
     * @return la prochaine prise à effectuer
     */
    private fun calculProchainPrise(prise: Prise): Prise {
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
        return prochainePrise
    }


    /**
     * Lance un algorithme pour trouver ce qui se rapproche le plus du nom du médicament
     * @param context contexte nécessaire à l'accès à la BD
     */
    private fun trouveNom(context: Context) {
        val algo = JaroWinkler()
        algo.getNomMedic(context)
        algo.aChercher = this.nomTraitement

        this.nomTraitement = algo.lancerDistance()
    }


    /**
     * Conformise l'unité du médicament
     */
    private fun trouveUnite() {
        if (this.suggFrequence?.isNotEmpty() == true) {
            val algo = JaroWinkler()
            algo.base = listOf(
                "besoin",
                "jour",
                "semaine",
                "mois"
            )
            algo.aChercher = suggFrequence as String

            val freq = algo.lancerDistance()
            this.frequencePrise = when (freq) {
                "besoin" -> EnumFrequence.AUBESOIN
                "jour" -> EnumFrequence.QUOTIDIEN
                "semaine" -> EnumFrequence.SEMAINE
                "mois" -> EnumFrequence.MOIS
                else -> EnumFrequence.AUBESOIN
            }
        }
    }


    /**
     * Remplie les variables dateDbtTraitement et dateFinTraitement
     */
    private fun trouveDuree() {
        if (this.suggDuree != null) {
            val test = this.suggDuree!!.split(" ")
            var entier = 1
            var mot: String? = null
            var resultAlgo: String

            val algo = JaroWinkler()
            algo.base = listOf(
                "jour",
                "semaine",
                "mois"
            )
            for (s: String in test) {
                algo.aChercher = s
                resultAlgo = algo.lancerDistance()

                if (resultAlgo.isNotEmpty()) {
                    mot = resultAlgo
                } else {
                    entier = try {
                        s.toInt()
                    } catch (e: NumberFormatException) {
                        1
                    }
                }
            }

            calculDuree(mot, entier)
        }
    }

    /**
     * Calcul la durée du traitement
     * @param mot le mot qui correspond à l'unité de temps
     * @param entier le nombre de jours/semaines/mois
     */
    private fun calculDuree(mot: String?, entier: Int) {
        this.dateDbtTraitement = LocalDate.now()
        if (mot != null) {
            when (mot) {
                "jour" -> this.dateFinTraitement =
                    this.dateDbtTraitement.plusDays(entier.toLong())

                "semaine" -> this.dateFinTraitement =
                    this.dateDbtTraitement.plusWeeks(entier.toLong())

                "mois" -> this.dateFinTraitement =
                    this.dateDbtTraitement.plusMonths(entier.toLong())
            }
        } else {
            this.dateFinTraitement = null
        }
    }

    /**
     * Créer un traitement à partir des données qu'il possède va notament appeler les méthodes privées
     * @param context context nécessaire pour accéder à la BD
     */
    fun paufine(context: Context) {
        trouveNom(context)
        trouveDuree()
        trouveUnite()
    }

    override fun toString(): String {
        return "Traitement(nomTraitement: '$nomTraitement', " +
                "codeCIS: '$codeCIS', " +
                "dosageNb: $dosageNb, " +
                "frequencePrise: '$frequencePrise', " +
                "dateFinTraitement: $dateFinTraitement, " +
                "typeComprime: '$typeComprime', " +
                "comprimesRestants: $comprimesRestants, " +
                "expire: $expire, " +
                "effetsSecondaires: $effetsSecondaires, " +
                "prises: $prises, " +
                "totalQuantite: $totalQuantite, " +
                "uuid: '$uuid', " +
                "uuidUser: '$uuidUser', " +
                "dateDbtTraitement: $dateDbtTraitement)"
    }
}

