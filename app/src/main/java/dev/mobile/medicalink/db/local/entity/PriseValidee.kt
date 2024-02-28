package dev.mobile.medicalink.db.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PriseValidee(
    @PrimaryKey val uuid: String,
    @ColumnInfo(name = "date") var date: String,
    @ColumnInfo(name = "uuidPrise") var uuidPrise: String,
    @ColumnInfo(name = "statut") var statut: String,
    )




