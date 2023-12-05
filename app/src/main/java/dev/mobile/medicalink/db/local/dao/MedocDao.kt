package dev.mobile.medicalink.db.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import dev.mobile.medicalink.db.local.entity.Medoc

@Dao
interface MedocDao {
    @Query("SELECT * FROM Medoc")
    fun getAll(): List<Medoc>

    @Query("SELECT * FROM Medoc WHERE uuid IN (:uuid)")
    fun getById(uuid: String): List<Medoc>

    @Query("SELECT * FROM Medoc WHERE uuidUser IN (:uuidUser)")
    fun getByUser(uuidUser: String): List<Medoc>

    @Insert
    fun insertAll(vararg Medocs: Medoc)

    @Delete
    fun delete(Medoc: Medoc)

    @Update
    fun update(Medoc: Medoc)

    @Query("SELECT Medoc.prises FROM Medoc WHERE uuidUser IN (:uuidUser)")
    fun getAllPrisesForAllMedocsOfOneUser(uuidUser: String): List<String>

}
