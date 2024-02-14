package dev.mobile.medicalink.db.local.dao

import androidx.room.*
import dev.mobile.medicalink.db.local.entity.CisBdpm


@Dao
interface CisBdpmDao {
    @Query("SELECT * FROM CisBdpm")
    fun getAll(): List<CisBdpm>

    @Query("SELECT * FROM CisBdpm WHERE codeCIS IN (:CodeCIS)")
    fun getById(CodeCIS: String): List<CisBdpm>

    @Insert
    fun insertAll(vararg CisBdpmDaos: CisBdpm)

    @Delete
    fun delete(CisBdpmDao: CisBdpm)

    @Update
    fun update(CisBdpmDao: CisBdpm)

    @Query("DELETE FROM CisBdpm")
    fun deleteAll()

}