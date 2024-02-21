package dev.mobile.medicalink.db.local.entity

import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import dev.mobile.medicalink.fragments.traitements.EnumFrequence
import dev.mobile.medicalink.fragments.traitements.Prise
import dev.mobile.medicalink.fragments.traitements.Traitement
import java.time.LocalDate

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["uuid"],
            childColumns = ["uuidUser"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CisBdpm::class,
            parentColumns = ["codeCIS"],
            childColumns = ["codeCIS"],
            onDelete = ForeignKey.CASCADE
        )
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
    @ColumnInfo(name = "dosageNB") val dosageNB: Int,
    @ColumnInfo(name = "frequencePrise") val frequencePrise: EnumFrequence,
    @ColumnInfo(name = "dateFinTraitement") var dateFinTraitement: LocalDate?,
    @ColumnInfo(name = "typeComprime") val typeComprime: String,
    @ColumnInfo(name = "comprimesRestants") var comprimesRestants: Int?,
    @ColumnInfo(name = "expire") var expire: Boolean,
    @ColumnInfo(name = "effetsSecondaires") val effetsSecondaires: String?,
    @ColumnInfo(name = "prises") val prises: String?,
    @ColumnInfo(name = "totalQuantite") val totalQuantite: Int?,
    @ColumnInfo(name = "dateDbtTraitement") val dateDbtTraitement: LocalDate,
) {
    fun toTraitement(): Traitement {
        return Traitement(
            nomTraitement = this.nom,
            codeCIS = this.codeCIS,
            dosageNb = this.dosageNB,
            frequencePrise = this.frequencePrise,
            dateFinTraitement = this.dateFinTraitement,
            typeComprime = this.typeComprime,
            comprimesRestants = this.comprimesRestants,
            expire = this.expire,
            effetsSecondaires = this.effetsSecondaires?.split(",")?.toMutableList(),
            prises = toPrises(this),
            totalQuantite = this.totalQuantite,
            uuid = this.uuid,
            uuidUser = this.uuidUser,
            dateDbtTraitement = this.dateDbtTraitement
        )
    }

    /**
     * Convertit une liste de prises en string en une liste de Prise
     * @param medoc le médicament associé aux prises
     * @return la liste de prises convertie
     */
    private fun toPrises(medoc: Medoc): MutableList<Prise> {
        val listePrise: MutableList<Prise> = mutableListOf()
        if (!medoc.prises.isNullOrEmpty()) {
            for (prise in medoc.prises.split("/")) {
                val traitementPrise: MutableList<String> = prise.split(";").toMutableList()
                Log.d("test", traitementPrise.toString())
                val maPrise = Prise(
                    traitementPrise[0],
                    traitementPrise[1],
                    traitementPrise[2].toInt(),
                    medoc.typeComprime
                )
                listePrise.add(maPrise)
            }
        }
        return listePrise
    }

}













