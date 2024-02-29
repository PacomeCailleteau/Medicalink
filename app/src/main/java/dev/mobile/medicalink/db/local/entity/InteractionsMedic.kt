package dev.mobile.medicalink.db.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class InteractionsMedic (
    @PrimaryKey val substance: String,
    val com: String,
    val interactions: List<Interactions>
)

data class Interactions(
    val substance: String,
    val com1: String,
    val com2: String
) {
    override fun toString(): String {
        return "$substance%$com1%$com2"
    }
}


