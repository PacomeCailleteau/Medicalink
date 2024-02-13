package dev.mobile.medicalink.db.local.dao

import androidx.room.Dao
import androidx.room.*
import dev.mobile.medicalink.db.local.entity.ContactMedecin

@Dao
interface ContactMedecinDao {
    @Query("SELECT * FROM ContactMedecin")
    fun getAll(): List<ContactMedecin>

    @Query("SELECT * FROM ContactMedecin WHERE rpps IN (:rpps)")
    fun getById(rpps: String): ContactMedecin?

    @Query("SELECT * FROM ContactMedecin WHERE userUuid IN (:userUuid)")
    fun getByUserUuid(userUuid: String): List<ContactMedecin>

    @Insert
    fun insertAll(vararg contactMedecin: ContactMedecin)

    @Delete
    fun delete(contactMedecin: ContactMedecin)

    @Update
    fun update(contactMedecin: ContactMedecin)

    @Query("DELETE FROM ContactMedecin")
    fun deleteAll()

}