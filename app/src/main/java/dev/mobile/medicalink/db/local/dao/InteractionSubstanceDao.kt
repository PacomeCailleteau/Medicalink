package dev.mobile.medicalink.db.local.dao

import androidx.room.*
import dev.mobile.medicalink.db.local.entity.InteractionSubstance


@Dao
interface InteractionSubstanceDao {
    @Query("SELECT * FROM InteractionSubstance")
    fun getAll(): List<InteractionSubstance>

    @Query("SELECT * FROM InteractionSubstance WHERE denominationSubstance IN (:denominationSubstance)")
    fun getOne(denominationSubstance: String): InteractionSubstance?

    @Insert
    fun insertAll(vararg interactionSubstance: InteractionSubstance)

    @Delete
    fun delete(interactionSubstance: InteractionSubstance)

    @Update
    fun update(interactionSubstance: InteractionSubstance)

    @Query("DELETE FROM InteractionSubstance")
    fun deleteAll()

}