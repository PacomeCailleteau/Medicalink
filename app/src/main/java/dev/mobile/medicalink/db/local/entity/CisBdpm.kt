package dev.mobile.medicalink.db.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class CisBdpm(
    @PrimaryKey val codeCIS: Int,
    var denomination: String,
    var formePharmaceutique: String,
    var voiesAdministration: String,
    var statutAdministratifAMM: String,
    var typeProcedureAMM: String,
    var etatCommercialisation: String,
    var dateAMM: String,
    var statutBdm: String,
    var numeroAutorisationEuropeenne: String,
    var titulaire: String,
    var surveillanceRenforcee: String,
)


