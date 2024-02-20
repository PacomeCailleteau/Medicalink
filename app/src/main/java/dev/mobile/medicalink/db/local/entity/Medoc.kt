package dev.mobile.medicalink.db.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import dev.mobile.medicalink.fragments.traitements.Prise
import dev.mobile.medicalink.fragments.traitements.Traitement
import java.time.LocalDate

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["uuid"],
            childColumns = ["uuidUser"],
            onDelete = ForeignKey.CASCADE),
        ForeignKey(
            entity = CisBdpm::class,
            parentColumns = ["codeCIS"],
            childColumns = ["codeCIS"],
            onDelete = ForeignKey.CASCADE)
    ],
    indices = [
        Index(value = ["uuidUser"]),
        Index(value = ["codeCIS"])
            ]
)

data class Medoc(
    @PrimaryKey val uuid: String,
    @ColumnInfo(name = "uuidUser") var uuidUser: String,
    @ColumnInfo(name = "nom") val nom: String,
    @ColumnInfo(name = "codeCIS") val codeCIS: String,
    @ColumnInfo(name = "dosageNB") val dosageNB: String,
    @ColumnInfo(name = "frequencePrise") val frequencePrise: String,
    @ColumnInfo(name = "dateFinTraitement") var dateFinTraitement: String?,
    @ColumnInfo(name = "typeComprime") val typeComprime: String,
    @ColumnInfo(name = "comprimesRestants") var comprimesRestants: Int?,
    @ColumnInfo(name = "expire") var expire: Boolean,
    @ColumnInfo(name = "effetsSecondaires") val effetsSecondaires: String?,
    @ColumnInfo(name = "prises") val prises: String?,
    @ColumnInfo(name = "totalQuantite") val totalQuantite: Int?,
    @ColumnInfo(name = "dateDbtTraitement") val dateDbtTraitement: String?,
) {
    fun toTraitement(): Traitement {
        return Traitement(
            nomTraitement = this.nom,
            codeCIS = this.codeCIS,
            dosageNb = try { this.dosageNB.toInt() } catch (e: NumberFormatException) { 0 },
            frequencePrise = this.frequencePrise,
            dateFinTraitement = this.dateFinTraitement?.let { LocalDate.parse(it) },
            typeComprime = this.typeComprime,
            comprimesRestants = this.comprimesRestants,
            expire = this.expire,
            effetsSecondaires = this.effetsSecondaires?.split(",")?.toMutableList(),
            prises = this.prises?.split(",")?.mapNotNull { priseString ->
                val priseParts = priseString.split(":")
                if (priseParts.size == 4) {
                    Prise(
                        numeroPrise = priseParts[0],
                        heurePrise = priseParts[1],
                        quantite = priseParts[2].toInt(),
                        typeComprime = priseParts[3]
                    )
                } else {
                    null // Gérer le cas où les données ne sont pas complètes
                }
            }?.toMutableList(),
            totalQuantite = this.totalQuantite,
            UUID = this.uuid,
            UUIDUSER = this.uuidUser,
            dateDbtTraitement = this.dateDbtTraitement?.let { LocalDate.parse(it) }
        )
    }

}













