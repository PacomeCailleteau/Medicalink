package dev.mobile.medicalink.db.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import dev.mobile.medicalink.db.local.entity.CisSubstance

@Dao
interface CisSubstanceDao {
    @Query("SELECT * FROM CisSubstance")
    fun getAll(): List<CisSubstance>

    @Query("SELECT * FROM CisSubstance WHERE CodeCIS IN (:codeCIS)")
    fun getById(codeCIS: String): CisSubstance

    @Query("SELECT * FROM CisSubstance WHERE codeSubstance IN (:codeSubstance)")
    fun getAllByCodeSubstance(codeSubstance: Int): List<CisSubstance>

    @Insert
    fun insertAll(vararg cisSubstances: CisSubstance)

    @Delete
    fun delete(cisSubstance: CisSubstance)

    @Update
    fun update(cisSubstance: CisSubstance)

    @Query("DELETE FROM CisSubstance")
    fun deleteAll()

}