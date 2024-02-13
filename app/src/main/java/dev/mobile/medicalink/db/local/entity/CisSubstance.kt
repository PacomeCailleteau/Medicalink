package dev.mobile.medicalink.db.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class CisSubstance (
    @PrimaryKey val codeCIS: Int,
    var elementPharmaceutique: String,
    var codeSubstance: Int,
    var denominationSubstance: String,
    var dosageSubstance: String,
    var referenceDosage: String,
    var natureComposant: String,
    var numeroLiaison: Int,
)





