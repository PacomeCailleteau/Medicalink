package dev.mobile.medicalink.db.local.repository

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import dev.mobile.medicalink.db.local.dao.StatutDouleurDao
import dev.mobile.medicalink.db.local.entity.StatutDouleur

class StatutDouleurRepository(private val statutDouleurDao: StatutDouleurDao) {

    companion object {
        private const val success = "Success"
        private const val statutDoesntExists = "StatutDouleur doesn't exists"
    }

    fun getAllStatutDouleur(): List<StatutDouleur> {
        return try {
            statutDouleurDao.getAll()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getOneStatutById(uuid: String): StatutDouleur? {
        return try {
            statutDouleurDao.getById(uuid)
        } catch (e: Exception) {
            null
        }
    }

    fun getStatutByUser(uuidUser: String): List<StatutDouleur> {
        return try {
            statutDouleurDao.getByUser(uuidUser)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun insertStatutDouleur(statutDouleur: StatutDouleur): Pair<Boolean, String> {
        return try {
            statutDouleurDao.insertAll(statutDouleur)
            Pair(true, success)
        } catch (e: SQLiteConstraintException) {
            Pair(false, statutDoesntExists)
        } catch (e: SQLiteException) {
            Pair(false, "Database Error : ${e.message}")
        } catch (e: Exception) {
            Pair(false, "Unknown Error : ${e.message}")
        }
    }

    fun deleteUser(statutDouleur: StatutDouleur): Pair<Boolean, String> {
        return try {
            statutDouleurDao.delete(statutDouleur)
            Pair(true, success)
        } catch (e: SQLiteConstraintException) {
            Pair(false, statutDoesntExists)
        } catch (e: SQLiteException) {
            Pair(false, "Database Error : ${e.message}")
        } catch (e: Exception) {
            Pair(false, "Unknown Error : ${e.message}")
        }
    }

    fun updateStatutDouleur(statutDouleur: StatutDouleur): Pair<Boolean, String> {
        return try {
            statutDouleurDao.update(statutDouleur)
            Pair(true, success)
        } catch (e: SQLiteConstraintException) {
            Pair(false, statutDoesntExists)
        } catch (e: SQLiteException) {
            Pair(false, "Database Error : ${e.message}")
        } catch (e: Exception) {
            Pair(false, "Unknown Error : ${e.message}")
        }
    }
}