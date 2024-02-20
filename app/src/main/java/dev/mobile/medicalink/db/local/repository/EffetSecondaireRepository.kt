package dev.mobile.medicalink.db.local.repository

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import dev.mobile.medicalink.db.local.dao.EffetSecondaireDao
import dev.mobile.medicalink.db.local.entity.EffetSecondaire

class EffetSecondaireRepository (private val EffetSecondaireDao: EffetSecondaireDao) {

    fun getAllEffetSecondaire(): List<EffetSecondaire> {
        return try {
            EffetSecondaireDao.getAll()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getEffetSecondairesByUuid(uuid: String): List<EffetSecondaire> {
        return try {
            EffetSecondaireDao.getByuuidUser(uuid)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun insertEffetSecondaire(EffetSecondaire: EffetSecondaire): Pair<Boolean, String> {
        return try {
            EffetSecondaireDao.insertAll(EffetSecondaire)
            Pair(true, "Success")
        } catch (e: SQLiteConstraintException) {
            Pair(false, "EffetSecondaire already exists")
        } catch (e: SQLiteException) {
            Pair(false, "Database Error : ${e.message}")
        } catch (e: Exception) {
            Pair(false, "Unknown Error : ${e.message}")
        }
    }

    fun deleteEffetSecondaire(EffetSecondaire: EffetSecondaire): Pair<Boolean, String> {
        return try {
            EffetSecondaireDao.delete(EffetSecondaire)
            Pair(true, "Success")
        } catch (e: SQLiteConstraintException) {
            Pair(false, "EffetSecondaire doesn't exist")
        } catch (e: SQLiteException) {
            Pair(false, "Database Error : ${e.message}")
        } catch (e: Exception) {
            Pair(false, "Unknown Error : ${e.message}")
        }
    }

    fun updateEffetSecondaire(EffetSecondaire: EffetSecondaire): Pair<Boolean, String> {
        return try {
            EffetSecondaireDao.update(EffetSecondaire)
            Pair(true, "Success")
        } catch (e: SQLiteConstraintException) {
            Pair(false, "EffetSecondaire doesn't exist")
        } catch (e: SQLiteException) {
            Pair(false, "Database Error : ${e.message}")
        } catch (e: Exception) {
            Pair(false, "Unknown Error : ${e.message}")
        }
    }

}