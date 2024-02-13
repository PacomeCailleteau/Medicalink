package dev.mobile.medicalink.db.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity
data class Contact(
    @PrimaryKey val uuid: String,
    val Rpps: Long,
    var firstName: String,
    var lastName: String,
    var fullname: String,
    var specialty: String?,
    var address: String?,
    var zipcode: String?,
    var city: String?,
    var phoneNumber: String?,
    var email: String?
) : Serializable


