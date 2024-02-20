package dev.mobile.medicalink.db.local.dao

import androidx.room.Insert
import androidx.room.Query
import dev.mobile.medicalink.db.local.entity.Interaction

interface InteractionDao {
    @Query("SELECT * FROM Interaction")
    fun getAll(): List<Interaction>

    @Query("SELECT * FROM Interaction WHERE substance IN (:substance)")
    fun getById(substance: Int): List<Interaction>

    @Insert
    fun insertAll(vararg InteractionDaos: Interaction)

}