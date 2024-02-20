package dev.mobile.medicalink.db.local.entity

import androidx.room.Entity

@Entity(primaryKeys = ["uuidUser", "uuidEffetSecondaire"])
data class EffetSecondaire(
    val uuidUser: String,
    val uuidEffetSecondaire: String,
    val titre: String,
    val message: String,
    val uriImage: String?,
    val date: String?
)