package dev.mobile.medicalink.db.local.repository

import android.database.sqlite.SQLiteException
import dev.mobile.medicalink.db.local.dao.InteractionSubstanceDao
import dev.mobile.medicalink.db.local.entity.InteractionSubstance

class InteractionSubstanceRepository(private val interactionSubstanceDao: InteractionSubstanceDao) {

    fun getAll(): List<InteractionSubstance> {
        return try {
            interactionSubstanceDao.getAll()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getOne(denominationSubstance: String): InteractionSubstance? {
        return try {
            interactionSubstanceDao.getOne(denominationSubstance)
        } catch (e: Exception) {
            null
        }
    }

    fun insertAll(interactionSubstance: InteractionSubstance): Pair<Boolean, String> {
        return try {
            interactionSubstanceDao.insertAll(interactionSubstance)
            Pair(true, "Success")
        } catch (e: SQLiteException) {
            Pair(false, "Database Error : ${e.message}")
        } catch (e: Exception) {
            Pair(false, "Unknown Error : ${e.message}")
        }
    }

    fun delete(interactionSubstance: InteractionSubstance): Pair<Boolean, String> {
        return try {
            interactionSubstanceDao.delete(interactionSubstance)
            Pair(true, "Success")
        } catch (e: SQLiteException) {
            Pair(false, "Database Error : ${e.message}")
        } catch (e: Exception) {
            Pair(false, "Unknown Error : ${e.message}")
        }
    }

    fun update(interactionSubstance: InteractionSubstance): Pair<Boolean, String> {
        return try {
            interactionSubstanceDao.update(interactionSubstance)
            Pair(true, "Success")
        } catch (e: SQLiteException) {
            Pair(false, "Database Error : ${e.message}")
        } catch (e: Exception) {
            Pair(false, "Unknown Error : ${e.message}")
        }
    }


}















