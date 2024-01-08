package dev.mobile.medicalink.db.local.repository

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import android.os.Build
import dev.mobile.medicalink.db.local.dao.PriseValideeDao
import dev.mobile.medicalink.db.local.dao.UserDao
import dev.mobile.medicalink.db.local.entity.PriseValidee
import dev.mobile.medicalink.db.local.entity.User
import dev.mobile.medicalink.fragments.traitements.Prise
import java.security.MessageDigest
import java.util.Base64


class PriseValideeRepository(private val priseValideeDao: PriseValideeDao) {

    fun getAllPriseValidee(): List<PriseValidee> {
        return try {
            priseValideeDao.getAll()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getOnePriseValideeById(uuid: String): List<PriseValidee> {
        return try {
            priseValideeDao.getById(uuid)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getByUUIDTraitementAndDate(date: String,uuidPrise: String): List<PriseValidee>{
        return try {
            priseValideeDao.getByUUIDTraitementAndDate(date,uuidPrise)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun insertPriseValidee(priseValidee: PriseValidee): Pair<Boolean, String> {
        return try {
            priseValideeDao.insertAll(priseValidee)
            Pair(true, "Success")
        } catch (e: SQLiteConstraintException) {
            Pair(false, "User already exists")
        } catch (e: SQLiteException) {
            Pair(false, "Database Error : ${e.message}")
        } catch (e: Exception) {
            Pair(false, "Unknown Error : ${e.message}")
        }
    }

    fun deletePriseValidee(priseValidee: PriseValidee): Pair<Boolean, String> {
        return try {
            priseValideeDao.delete(priseValidee)
            Pair(true, "Success")
        } catch (e: SQLiteConstraintException) {
            Pair(false, "User doesn't exist")
        } catch (e: SQLiteException) {
            Pair(false, "Database Error : ${e.message}")
        } catch (e: Exception) {
            Pair(false, "Unknown Error : ${e.message}")
        }
    }

    fun updatePriseValidee(priseValidee: PriseValidee): Pair<Boolean, String> {
        return try {
            priseValideeDao.update(priseValidee)
            Pair(true, "Success")
        } catch (e: SQLiteConstraintException) {
            Pair(false, "User doesn't exist")
        } catch (e: SQLiteException) {
            Pair(false, "Database Error : ${e.message}")
        } catch (e: Exception) {
            Pair(false, "Unknown Error : ${e.message}")
        }
    }

}
