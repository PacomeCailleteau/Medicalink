package dev.mobile.medicalink.db.local.dao

import androidx.room.*
import dev.mobile.medicalink.db.local.entity.Medoc

@Dao
interface MedocDao {
    @Query("SELECT * FROM Medoc")
    fun getAll(): List<Medoc>

    @Query("SELECT * FROM Medoc WHERE uuid IN (:uuid)")
    fun getById(uuid: String): List<Medoc>

    @Query("SELECT * FROM Medoc WHERE uuidUser IN (:uuidUser)")
    fun getByUser(uuidUser: String): List<Medoc>

    @Query("SELECT * FROM Medoc WHERE CodeCIS IN (:codeCIS)")
    fun getByCIS(codeCIS: String): Medoc?

    @Insert
    fun insertAll(vararg medocs: Medoc)

    @Delete
    fun delete(medoc: Medoc)

    @Update
    fun update(medoc: Medoc)

}
