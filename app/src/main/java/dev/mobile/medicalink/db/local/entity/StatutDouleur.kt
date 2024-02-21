package dev.mobile.medicalink.db.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["uuid"],
            childColumns = ["uuidUser"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["uuidUser"])
    ]
)
data class StatutDouleur(
    @PrimaryKey val uuid: String,
    @ColumnInfo(name = "raison") var raison: String,
    @ColumnInfo(name = "avantPrise") var avantPrise: Boolean?,
    @ColumnInfo(name = "date") var date: String,
    @ColumnInfo(name = "type") var type: String,
    @ColumnInfo(name = "valeur") var valeur: Int,
    @ColumnInfo(name = "uuidUser") val uuidUser: String
) {
    fun estComforme() {
        assert(
            this.type in listOf(
                "medicament",
                "intervalle",
                "spontane"
            )
        ) { "Type invalid dans StatutDouleur" }
        assert(this.valeur in 0..10) { "Valeur invalid dans StatutDouleur" }

        if (this.type == "medicament") {
            assert(this.avantPrise != null) { "Un StatutDouleur provenant d'un médicament ne peut pas avoir 'avantPrise' vide" }
        } else {
            assert(this.avantPrise == null) { "Seul les StatutDouleur provenant d'un médicament peuvent avoir une valeur dans 'avantPrise'" }
        }
    }
}