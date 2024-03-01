package dev.mobile.medicalink.db.local.dao

import androidx.room.*
import dev.mobile.medicalink.db.local.entity.CisBdpm


@Dao
interface CisBdpmDao {
    @Query("SELECT * FROM CisBdpm")
    fun getAll(): List<CisBdpm>

    @Query("SELECT * FROM CisBdpm WHERE codeCIS IN (:codeCIS)")
    fun getById(codeCIS: String): List<CisBdpm>

    @Query("SELECT * FROM CisBdpm WHERE denomination in (:nomMedoc)")
    fun getByName(nomMedoc: String): List<CisBdpm>

    @Insert
    fun insertAll(vararg cisBdpmDaos: CisBdpm)

    @Delete
    fun delete(cisBdpmDao: CisBdpm)

    @Update
    fun update(cisBdpmDao: CisBdpm)

    @Query("DELETE FROM CisBdpm")
    fun deleteAll()

}