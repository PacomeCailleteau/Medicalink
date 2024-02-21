package dev.mobile.medicalink.db.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.security.InvalidParameterException
import java.time.LocalDate

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
    fun toDate(variable: String): LocalDate {
        return when (variable) {
            "intervalle" -> LocalDate.parse(this.intervalle)
            "debutJournee" -> LocalDate.parse(this.debutJournee)
            "finJournee" -> LocalDate.parse(this.finJournee)
            else -> {
                throw InvalidParameterException("Le paramètre doit être compris dans: 'intervalle', 'debutJournee', 'finJournee'")
            }
        }
    }
}




