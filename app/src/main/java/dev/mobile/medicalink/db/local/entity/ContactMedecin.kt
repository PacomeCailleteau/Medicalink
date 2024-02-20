package dev.mobile.medicalink.db.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import dev.mobile.medicalink.utils.medecin.Medecin

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["uuid"],
            childColumns = ["userUuid"],
            onDelete = ForeignKey.CASCADE
        )],
    indices = [Index(value = ["userUuid"])],
)


data class ContactMedecin(
    @PrimaryKey val rpps: String,
    val userUuid: String,
    val firstname: String,
    val lastname: String,
    val specialty: String,
    val email: String,
    val phoneNumber: String,
    val address: String,
    val zipCode: String,
    val city: String,
    val gender: String,
) {
    fun asMedecin(): Medecin {
        return Medecin(
            rpps = this.rpps,
            firstname = this.firstname,
            lastname = this.lastname,
            specialty = this.specialty,
            email = this.email,
            phoneNumber = this.phoneNumber,
            address = this.address,
            zipCode = this.zipCode,
            city = this.city,
            gender = this.gender,
        )
    }
}