package dev.mobile.medicalink.db.local.repository

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import android.os.Build
import dev.mobile.medicalink.db.local.dao.UserDao
import dev.mobile.medicalink.db.local.entity.User
import java.security.MessageDigest
import java.util.Base64


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

    fun getUsersConnected(isConnected: Boolean = true): List<User> {
        return try {
            userDao.getByConnected(isConnected)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun insertUser(user: User) : Pair<Boolean, String> {
        val hashedPassword = hashPassword(user.password!!)
        user.password = hashedPassword
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
        //TODO("hash password if needed")
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

    private fun hashPassword(password: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val input = password.toByteArray(Charsets.UTF_8)
        val bytes = md.digest(input)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Base64.getUrlEncoder().encodeToString(bytes)
        } else {
            TODO("VERSION.SDK_INT < O")
        }
    }

}