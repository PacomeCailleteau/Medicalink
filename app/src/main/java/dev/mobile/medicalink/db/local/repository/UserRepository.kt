package dev.mobile.medicalink.db.local.repository

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import dev.mobile.medicalink.db.local.dao.UserDao
import dev.mobile.medicalink.db.local.entity.User

class UserRepository(private val userDao: UserDao) {

    fun getAllUsers(): List<User> {
        return try {
            userDao.getAll()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getOneUserById(uuid: String): List<User> {
        return try {
            userDao.getById(uuid)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun insertUser(user: User) : Pair<Boolean, String> {
        return try {
            userDao.insertAll(user)
            Pair(true, "Success")
        } catch (e: SQLiteConstraintException) {
            Pair(false, "User already exists")
        } catch (e: SQLiteException) {
            Pair(false, "Database Error : ${e.message}")
        } catch (e: Exception) {
            Pair(false, "Unknown Error : ${e.message}")
        }
    }

    fun deleteUser(user: User) : Pair<Boolean, String> {
        return try {
            userDao.delete(user)
            Pair(true, "Success")
        } catch (e: SQLiteConstraintException) {
            Pair(false, "User doesn't exist")
        } catch (e: SQLiteException) {
            Pair(false, "Database Error : ${e.message}")
        } catch (e: Exception) {
            Pair(false, "Unknown Error : ${e.message}")
        }
    }

    fun updateUser(user: User) : Pair<Boolean, String> {
        return try {
            userDao.update(user)
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