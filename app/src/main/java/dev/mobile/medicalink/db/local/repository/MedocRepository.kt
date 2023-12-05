package dev.mobile.medicalink.db.local.repository

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import dev.mobile.medicalink.db.local.dao.MedocDao
import dev.mobile.medicalink.db.local.entity.Medoc

class MedocRepository(private val medocDao: MedocDao) {


    fun getAllMedocs(): List<Medoc> {
        return try {
            medocDao.getAll()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getOneMedocById(uuid: String): List<Medoc> {
        return try {
            medocDao.getById(uuid)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getAllMedocByUserId(uuidUser: String): List<Medoc> {
        return try {
            medocDao.getByUser(uuidUser)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun insertMedoc(medoc: Medoc): Pair<Boolean, String> {
        return try {
            medocDao.insertAll(medoc)
            Pair(true, "Success")
        } catch (e: SQLiteConstraintException) {
            Pair(false, "Medoc already exists")
        } catch (e: SQLiteException) {
            Pair(false, "Database Error : ${e.message}")
        } catch (e: Exception) {
            Pair(false, "Unknown Error : ${e.message}")
        }
    }

    fun deleteMedoc(medoc: Medoc): Pair<Boolean, String> {
        return try {
            medocDao.delete(medoc)
            Pair(true, "Success")
        } catch (e: SQLiteConstraintException) {
            Pair(false, "Medoc doesn't exist")
        } catch (e: SQLiteException) {
            Pair(false, "Database Error : ${e.message}")
        } catch (e: Exception) {
            Pair(false, "Unknown Error : ${e.message}")
        }
    }

    fun updateMedoc(medoc: Medoc): Pair<Boolean, String> {
        return try {
            medocDao.update(medoc)
            Pair(true, "Success")
        } catch (e: SQLiteConstraintException) {
            Pair(false, "Medoc doesn't exist")
        } catch (e: SQLiteException) {
            Pair(false, "Database Error : ${e.message}")
        } catch (e: Exception) {
            Pair(false, "Unknown Error : ${e.message}")
        }
    }

    fun getAllPrisesForAllMedocsOfOneUser(uuidUser: String): List<String> {
        return medocDao.getAllPrisesForAllMedocsOfOneUser(uuidUser)
    }
}