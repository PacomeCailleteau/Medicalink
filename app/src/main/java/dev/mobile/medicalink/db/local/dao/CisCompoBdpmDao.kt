package dev.mobile.medicalink.db.local.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import dev.mobile.medicalink.db.local.entity.CisBdpm
import dev.mobile.medicalink.db.local.entity.CisCompoBdpm

interface CisCompoBdpmDao {
    @Query("SELECT * FROM CisCompoBdpm")
    fun getAll(): List<CisBdpm>

    @Query("SELECT * FROM CisCompoBdpm WHERE CodeCIS IN (:CodeCIS)")
    fun getById(CodeCIS: Int): List<CisBdpm>

    @Insert
    fun insertAll(vararg CisCompoBdpmDaos: CisCompoBdpm)

    @Delete
    fun delete(CisCompoBdpmDao: CisCompoBdpm)

    @Update
    fun update(CisCompoBdpmDao: CisCompoBdpm)

    @Query("DELETE FROM CisCompoBdpm")
    fun deleteAll()
}