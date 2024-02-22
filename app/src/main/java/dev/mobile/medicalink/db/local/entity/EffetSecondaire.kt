package dev.mobile.medicalink.db.local.entity

import androidx.room.Entity
import java.io.Serializable

@Entity(primaryKeys = ["uuidUser", "uuidEffetSecondaire"])
data class EffetSecondaire(
    val uuidUser: String,
    val uuidEffetSecondaire: String,
    val titre: String,
    val message: String,
    val date: String
) : Serializable