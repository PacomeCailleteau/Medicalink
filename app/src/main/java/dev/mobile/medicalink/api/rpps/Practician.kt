package dev.mobile.medicalink.api.rpps

data class Practician(
    var rpps: Long,
    var firstName: String,
    var lastName: String,
    var fullName: String,
    var specialty: String?,
    var address: String?,
    var zipcode: String?,
    var city: String?,
    var phoneNumber: String?
)