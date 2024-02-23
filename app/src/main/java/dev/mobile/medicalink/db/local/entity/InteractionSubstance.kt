package dev.mobile.medicalink.db.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class InteractionSubstance (
    @PrimaryKey val denominationSubstance: String,
    val petitCommentaire: String,
    val listeDeInteraction: String
)




