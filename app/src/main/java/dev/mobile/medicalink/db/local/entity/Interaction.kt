package dev.mobile.medicalink.db.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Interaction(
    @PrimaryKey val substance: String,
    val incompatibles: String
)