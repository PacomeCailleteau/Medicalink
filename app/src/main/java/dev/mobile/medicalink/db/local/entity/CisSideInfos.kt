package dev.mobile.medicalink.db.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class CisSideInfos (
    @PrimaryKey val CodeCIS: Int,
    var contreIndications: String,
    var allergies: String,
        )