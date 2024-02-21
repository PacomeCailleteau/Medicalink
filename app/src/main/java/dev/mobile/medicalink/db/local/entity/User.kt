package dev.mobile.medicalink.db.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.security.InvalidKeyException
import java.security.InvalidParameterException

@Entity
data class User(
    @PrimaryKey val uuid: String,
    @ColumnInfo(name = "statut") val statut: String,
    @ColumnInfo(name = "nom") val nom: String,
    @ColumnInfo(name = "prenom") val prenom: String,
    @ColumnInfo(name = "dateDeNaissance") val dateDeNaissance: String,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "password") var password: String,
    @ColumnInfo(name = "isConnected") var isConnected: Boolean,
    @ColumnInfo(name = "intervalle") var intervalle: String,
    @ColumnInfo(name = "debutJournee") var debutJournee: String,
    @ColumnInfo(name = "finJournee") var finJournee: String
) {
    fun dateComforme() {
        var test = this.intervalle.split(":")
        assert(test.size == 2) { "Le format de l'intervale doit être X:Y où X et Y sont des int qui représente les heures et les minutes." }
        try {
            assert(test[0].toInt() >= 0) { "Les heures ne peuvent pas être négatives" }
            assert(test[1].toInt() in 0..59) { "Les minutes doivent être comprises entre 0 et 59" }
        } catch (_: Exception) {
            throw InvalidKeyException("Le format de l'intervale doit être X:Y où X et Y sont des int qui représente les heures et les minutes.")
        }

        test = this.debutJournee.split(":")
        assert(test.size == 2) { "Le format de 'debutJournee' doit être X:Y où X et Y sont des int qui représente les heures et les minutes." }
        try {
            assert(test[0].toInt() >= 0) { "Les heures ne peuvent pas être négatives" }
            assert(test[1].toInt() in 0..59) { "Les minutes doivent être comprises entre 0 et 59" }
        } catch (_: Exception) {
            throw InvalidKeyException("Le format de 'debutJournee' doit être X:Y où X et Y sont des int qui représente les heures et les minutes.")
        }

        test = this.finJournee.split(":")
        assert(test.size == 2) { "Le format de 'finJournee' doit être X:Y où X et Y sont des int qui représente les heures et les minutes." }
        try {
            assert(test[0].toInt() >= 0) { "Les heures ne peuvent pas être négatives" }
            assert(test[1].toInt() in 0..59) { "Les minutes doivent être comprises entre 0 et 59" }
        } catch (_: Exception) {
            throw InvalidKeyException("Le format de 'finJournee' doit être X:Y où X et Y sont des int qui représente les heures et les minutes.")
        }
    }

    fun toPair(variable: String): Pair<Int, Int> {
        dateComforme()
        val result = when (variable) {
            "intervalle" -> this.intervalle.split(":")
            "debutJournee" -> this.debutJournee.split(":")
            "finJournee" -> this.debutJournee.split(":")
            else -> {
                throw InvalidParameterException("Le paramètre doit être compris dans: 'intervalle', 'debutJournee', 'finJournee'")
            }
        }
        val retour = result.map { it.toInt() }
        return Pair(retour[0], retour[1])
    }
}




