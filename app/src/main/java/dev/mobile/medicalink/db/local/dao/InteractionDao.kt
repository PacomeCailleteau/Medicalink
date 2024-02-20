package dev.mobile.medicalink.db.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.mobile.medicalink.db.local.entity.Interaction

@Dao
interface InteractionDao {
    @Query("SELECT * FROM Interaction")
    fun getAll(): List<Interaction>

    @Query("SELECT * FROM Interaction WHERE substance IN (:substance)")
    fun getBySubstance(substance: Int): List<Interaction>

    @Insert
    fun insertAll(vararg InteractionDaos: Interaction)

}