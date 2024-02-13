package dev.mobile.medicalink.db.local.dao

import androidx.room.*
import dev.mobile.medicalink.db.local.entity.Contact


@Dao
interface ContactDao {
    @Query("SELECT * FROM Contact")
    fun getAll(): List<Contact>

    @Query("SELECT * FROM Contact WHERE rpps IN (:Rpps)")
    fun getById(Rpps: Int): List<Contact>

    @Insert
    fun insertAll(vararg ContactDaos: Contact)

    @Delete
    fun delete(ContactDao: Contact)

    @Update
    fun update(ContactDao: Contact)

    @Query("DELETE FROM Contact")
    fun deleteAll()

}