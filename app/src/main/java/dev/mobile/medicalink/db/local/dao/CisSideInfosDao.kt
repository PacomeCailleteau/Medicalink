package dev.mobile.medicalink.db.local.dao

import androidx.room.*
import dev.mobile.medicalink.db.local.entity.CisSideInfos


@Dao
interface CisSideInfosDao {
    @Query("SELECT * FROM CisSideInfos")
    fun getAll(): List<CisSideInfos>

    @Query("SELECT * FROM CisSideInfos WHERE CodeCIS IN (:CodeCIS)")
    fun getById(CodeCIS: Int): List<CisSideInfos>

    @Insert
    fun insertAll(vararg CisSideInfosDaos: CisSideInfos)

    @Delete
    fun delete(CisSideInfosDao: CisSideInfos)

    @Update
    fun update(CisSideInfosDao: CisSideInfos)

    @Query("DELETE FROM CisSideInfos")
    fun deleteAll()

}