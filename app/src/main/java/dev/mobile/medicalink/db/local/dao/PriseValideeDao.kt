package dev.mobile.medicalink.db.local.dao

import androidx.room.*
import dev.mobile.medicalink.db.local.entity.PriseValidee
import dev.mobile.medicalink.db.local.entity.User

@Dao
interface PriseValideeDao {
    @Query("SELECT * FROM prisevalidee")
    fun getAll(): List<PriseValidee>

    @Query("SELECT * FROM prisevalidee WHERE uuid IN (:uuid)")
    fun getById(uuid: String): List<PriseValidee>

    @Query("SELECT * FROM prisevalidee WHERE date IN (:date) AND uuidPrise IN (:uuidPrise)")
    fun getByUUIDTraitementAndDate(date: String,uuidPrise: String): List<PriseValidee>

    @Insert
    fun insertAll(vararg priseValidee: PriseValidee)

    @Delete
    fun delete(priseValidee: PriseValidee)

    @Update
    fun update(priseValidee: PriseValidee)


}
