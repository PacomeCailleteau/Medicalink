package dev.mobile.medicalink.db.local.repository

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import android.util.Log
import dev.mobile.medicalink.db.local.dao.ContactDao
import dev.mobile.medicalink.db.local.entity.Contact

class ContactRepository(private val ContactDao: ContactDao) {

    fun getAllContact(): List<Contact> {
        return try {
            ContactDao.getAll()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getOneContactById(Rpps: Int): List<Contact> {
        return try {
            ContactDao.getById(Rpps)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun insertContact(Contact: Contact): Pair<Boolean, String> {
        return try {
            ContactDao.insertAll(Contact)
            Pair(true, "Success")
        } catch (e: SQLiteConstraintException) {
            Pair(false, "Contact already exists")
        } catch (e: SQLiteException) {
            Pair(false, "Database Error : ${e.message}")
        } catch (e: Exception) {
            Pair(false, "Unknown Error : ${e.message}")
        }
    }

    fun deleteContact(Contact: Contact): Pair<Boolean, String> {
        return try {
            ContactDao.delete(Contact)
            Pair(true, "Success")
        } catch (e: SQLiteConstraintException) {
            Pair(false, "Contact doesn't exist")
        } catch (e: SQLiteException) {
            Pair(false, "Database Error : ${e.message}")
        } catch (e: Exception) {
            Pair(false, "Unknown Error : ${e.message}")
        }
    }

    fun updateContact(Contact: Contact): Pair<Boolean, String> {
        return try {
            ContactDao.update(Contact)
            Pair(true, "Success")
        } catch (e: SQLiteConstraintException) {
            Pair(false, "Contact doesn't exist")
        } catch (e: SQLiteException) {
            Pair(false, "Database Error : ${e.message}")
        } catch (e: Exception) {
            Pair(false, "Unknown Error : ${e.message}")
        }
    }

}