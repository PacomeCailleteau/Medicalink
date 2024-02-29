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
    @ColumnInfo(name = "type") var type: EnumTypeStatut,
    // Pour uuidMedoc et avantPrise, ils ne doivent être rempli que si le type == Medicament
    @ColumnInfo(name = "uuidMedoc") var uuidMedoc: String?,
    @ColumnInfo(name = "avantPrise") var avantPrise: Boolean?,
    // string de LocalDateTime
    @ColumnInfo(name = "date") var date: String,
    @ColumnInfo(name = "valeur") var valeur: Int,
    @ColumnInfo(name = "uuidUser") val uuidUser: String
)