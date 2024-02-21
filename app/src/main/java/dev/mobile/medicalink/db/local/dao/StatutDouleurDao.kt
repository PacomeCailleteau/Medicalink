package dev.mobile.medicalink.db.local.dao

import androidx.room.*
import dev.mobile.medicalink.db.local.entity.StatutDouleur

@Dao
interface StatutDouleurDao {
    @Query("SELECT * FROM statutdouleur")
    fun getAll(): List<StatutDouleur>

    @Query("SELECT * FROM statutdouleur WHERE uuid IN (:uuid)")
    fun getById(uuid: String): StatutDouleur?

    @Insert
    fun insertAll(vararg statuts: StatutDouleur)

    @Delete
    fun delete(statutDouleur: StatutDouleur)

    @Update
    fun update(statutDouleur: StatutDouleur)
}
