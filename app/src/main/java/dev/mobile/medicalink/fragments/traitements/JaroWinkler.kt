package dev.mobile.medicalink.fragments.traitements

import android.content.Context
import dev.mobile.medicalink.db.local.AppDatabase
import dev.mobile.medicalink.db.local.repository.CisBdpmRepository
import java.util.Arrays

class JaroWinkler {

    var aChercher: String
    var base: List<String>

    init {
        this.aChercher = ""
        this.base = listOf()
    }

    private fun estPret(): Boolean {
        return (this.aChercher != "" && this.base.isNotEmpty())
    }

    /**
     * Lance l'algorithme de recherche
     * @return le mot le plus proche en String, s'il n'y en a pas, il renvoie un string vide
     */
    fun lancerDistance(): String {
        if (!estPret()) {
            throw Exception("Les variables ne sont pas remplies.")
        }
        val result = withinDistance(this.base, 0.15, this.aChercher)
        return result.word
    }

    /**
     * Vérifie pour chaque mot de la base s'ils sont dans la range choisi en paramètre
     * @param words liste de mot composant la base de recherche
     * @param maxDistance distance maximale accepté comme étant un résultat pertinent
     * @param string mot recherché
     * @return le mot le plus proche sous forme de 'StringDistance' et s'il n'y en a pas, un 'StringDistance' mais vide
     */
    private fun withinDistance(
        words: List<String>,
        maxDistance: Double,
        string: String
    ): StringDistance {

        val result: MutableList<StringDistance> = ArrayList()
        for (word: String in words) {
            val distance = jaroWinklerDistance(word, string)
            if (distance <= maxDistance) result.add(StringDistance(word, distance))
        }
        result.sort()

        if (result.isEmpty()) {
            return StringDistance("", 0.0)
        }

        return result[0]
    }

    /**
     * Calcule la distance entre deux mots selon l'algorithme de JaroWinkler
     * @param str1 mot dont on cherche à trouver le mot le plus proche
     * @param str2 mot qui peut potentiellement être le mot le plus proche de 'str1'
     * @return result distance entre les deux mots en Double
     */
    private fun jaroWinklerDistance(str1: String, str2: String): Double {
        var string1 = str1
        var string2 = str2
        var len1 = string1.length
        var len2 = string2.length
        if (len1 < len2) {
            val s = string1
            string1 = string2
            string2 = s
            val tmp = len1
            len1 = len2
            len2 = tmp
        }
        if (len2 == 0) return if (len1 == 0) 0.0 else 1.0
        val delta = 1.coerceAtLeast(len1 / 2) - 1
        val flag = BooleanArray(len2)
        Arrays.fill(flag, false)
        val ch1Match = CharArray(len1)
        var matches = 0
        for (i in 0 until len1) {
            val ch1 = string1[i]
            for (j in 0 until len2) {
                val ch2 = string2[j]
                if ((j <= i + delta) && (j + delta >= i) && (ch1 == ch2) && !flag[j]) {
                    flag[j] = true
                    ch1Match[matches++] = ch1
                    break
                }
            }
        }
        if (matches == 0) return 1.0
        var transpositions = 0
        run {
            var i = 0
            var j = 0
            while (j < len2) {
                if (flag[j]) {
                    if (string2[j] != ch1Match[i]) ++transpositions
                    ++i
                }
                ++j
            }
        }
        val m = matches.toDouble()
        val jaro = ((m / len1) + (m / len2) + ((m - transpositions / 2.0) / m)) / 3.0
        var commonPrefix = 0
        len2 = 4.coerceAtMost(len2)
        for (i in 0 until len2) {
            if (string1[i] == string2[i]) ++commonPrefix
        }
        return 1.0 - (jaro + commonPrefix * 0.1 * (1.0 - jaro))
    }

    /**
     * Remplie la base de recherche avec tous les noms de médicaments
     * @param context contexte nécessaire pour accéder à la BD
     */
    fun getNomMedic(context: Context) {
        val db = AppDatabase.getInstance(context)
        val cisInterface = CisBdpmRepository(db.cisBdpmDao())
        val res = cisInterface.getAllCisBdpm()

        base = res.map { it.denomination }
    }

    /**
     * Classe interne qui stock les résultats de l'algorithme
     * @param word mot en string
     * @param distance distance avec le mot recherché
     */
    private inner class StringDistance(val word: String, val distance: Double) :
        Comparable<StringDistance?> {
        override fun compareTo(other: StringDistance?): Int {
            if (other == null) {
                return -1
            }
            return distance.compareTo(other.distance)
        }
    }
}