package dev.mobile.medicalink.utils.medecin

import dev.mobile.medicalink.db.local.entity.ContactMedecin


data class Medecin(
    val rpps: String,
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
    fun asContactMedecin(userUuid: String): ContactMedecin {
        return ContactMedecin(
            rpps = this.rpps,
            userUuid = userUuid,
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