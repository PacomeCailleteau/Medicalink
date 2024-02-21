package dev.mobile.medicalink.db.local.entity

import androidx.room.Entity
import dev.mobile.medicalink.api.rpps.Practician
import java.io.Serializable

@Entity(primaryKeys = ["uuid", "rpps"])
data class Contact(
    val uuid: String,
    val rpps: Long,
    var firstName: String,
    var lastName: String,
    var fullName: String,
    var specialty: String?,
    var address: String?,
    var zipcode: String?,
    var city: String?,
    var phoneNumber: String?,
    var email: String?
) : Serializable {
    companion object {
        fun fromPractician(uuid: String, practician: Practician): Contact {
            return Contact(
                uuid,
                practician.rpps,
                practician.firstName,
                practician.lastName,
                practician.fullName,
                practician.specialty,
                practician.address,
                practician.zipcode,
                practician.city,
                practician.phoneNumber,
                null
            )
        }
    }
}


