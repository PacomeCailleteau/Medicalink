package dev.mobile.medicalink.db.local.dao

import androidx.room.*
import dev.mobile.medicalink.db.local.entity.Contact


@Dao
interface ContactDao {
    @Query("SELECT * FROM Contact")
    fun getAll(): List<Contact>

    @Query("SELECT * FROM Contact WHERE rpps IN (:Rpps)")
    fun getById(Rpps: Int): List<Contact>

    @Query("SELECT * FROM Contact WHERE uuid IN (:uuid)")
    fun getByUuid(uuid: String): List<Contact>

    @Query("SELECT * FROM Contact WHERE rpps=(:Rpps) AND uuid=(:uuid)")
    fun getByIdAndUuid(Rpps: Long, uuid: String): Contact

    @Query("SELECT * FROM Contact WHERE rpps IN (:Rpps) AND uuid IN (:uuid)")
    fun getByIdAndUuid(Rpps: Int, uuid: String): Contact

    @Insert
    fun insertAll(vararg ContactDaos: Contact)

    @Delete
    fun delete(ContactDao: Contact)

    @Update
    fun update(ContactDao: Contact)

    @Query("DELETE FROM Contact")
    fun deleteAll()

}