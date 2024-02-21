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
        ),
        ForeignKey(
            entity = Medoc::class,
            parentColumns = ["uuid"],
            childColumns = ["uuidMedoc"]
        )
    ],
    indices = [
        Index(value = ["uuidUser"]),
        Index(value = ["uuidMedoc"])
    ]
)
data class StatutDouleur(
    @PrimaryKey val uuid: String,
    @ColumnInfo(name = "uuidMedoc") var uuidMedoc: String?,
    @ColumnInfo(name = "avantPrise") var avantPrise: Boolean?,
    @ColumnInfo(name = "date") var date: String,
    @ColumnInfo(name = "type") var type: EnumTypeStatut,
    @ColumnInfo(name = "valeur") var valeur: Int,
    @ColumnInfo(name = "uuidUser") val uuidUser: String
) {
    fun estComforme() {
        assert(this.valeur in 0..10) { "Valeur invalid dans StatutDouleur" }

        if (this.type == EnumTypeStatut.Medicament) {
            assert(this.avantPrise != null) { "Un StatutDouleur provenant d'un médicament ne peut pas avoir 'avantPrise' vide" }
            assert(this.uuidMedoc != null) { "Il doit y avoir un médicament si le type est 'Medicament'"}
        } else {
            assert(this.avantPrise == null) { "Seul les StatutDouleur provenant d'un médicament peuvent avoir une valeur dans 'avantPrise'" }
            assert(this.uuidMedoc == null) { "Il ne doit pas y avoir de médicament si le type n'est pas 'avantPrise'"}
        }
    }
}