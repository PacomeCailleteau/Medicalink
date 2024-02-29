package dev.mobile.medicalink.db.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import dev.mobile.medicalink.db.local.entity.InteractionsMedic

@Dao
interface InteractionsMedicDao {
    @Query("SELECT * FROM InteractionsMedic")
    fun getAll(): List<InteractionsMedic>

    @Query("SELECT * FROM InteractionsMedic WHERE substance IN (:substance)")
    fun getAllBySubstance(substance: String): List<InteractionsMedic>

    @Insert
    fun insertAll(vararg interactionsMedic: InteractionsMedic)

    @Delete
    fun delete(interactionsMedic: InteractionsMedic)

    @Query("DELETE FROM InteractionsMedic")
    fun deleteAll()

    @Update
    fun update(interactionsMedic: InteractionsMedic)
}