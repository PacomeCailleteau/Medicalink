package dev.mobile.medicalink.db.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.mobile.medicalink.db.local.entity.Interaction

@Dao
interface InteractionDao {
    //Interaction = Name Name2
    //substance = Name
    //%substance% = liste de Interaction
    @Query("SELECT * FROM Interaction WHERE substance LIKE '%' || :substance || '%'")
    fun getAllLike(substance: String): List<Interaction>

    @Query("SELECT * FROM Interaction WHERE substance IN (:substance)")
    fun getBySubstance(substance: String): List<Interaction>

    @Insert
    fun insertAll(vararg InteractionDaos: Interaction)

}