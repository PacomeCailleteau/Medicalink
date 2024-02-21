package dev.mobile.medicalink.db.local.dao

import androidx.room.*
import dev.mobile.medicalink.db.local.entity.StatutDouleur
import dev.mobile.medicalink.db.local.entity.User

@Dao
interface StatutDouleurDao {
    @Query("SELECT * FROM statutdouleur")
    fun getAll(): StatutDouleur?

    @Query("SELECT * FROM statutdouleur WHERE uuid IN (:uuid)")
    fun getById(uuid: String): StatutDouleur?

    @Insert
    fun insertAll(vararg statuts: StatutDouleur)

    @Delete
    fun delete(statutDouleur: StatutDouleur)

    @Update
    fun update(statutDouleur: StatutDouleur)


}
