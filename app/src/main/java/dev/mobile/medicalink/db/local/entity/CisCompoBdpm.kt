package dev.mobile.medicalink.db.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CisCompoBdpm(
    @PrimaryKey val CodeCIS: Int,
    var designationForme: String,
    var codeSubstance : String,
    var denomination : String,
    var dosage : String,
    var referenceDosage : String,
    var natureComposant : String,
    var numeroLiaisonSAFT: String
)