package dev.mobile.medicalink.db.local.repository

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import android.os.Build
import dev.mobile.medicalink.db.local.dao.UserDao
import dev.mobile.medicalink.db.local.entity.User
import java.security.MessageDigest
import java.util.Base64


class UserRepository(private val userDao: UserDao) {

    companion object {
        private const val success = "Success"
        private const val userDoesntExists = "User doesn't exists"
    }

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

    fun insertUser(user: User): Pair<Boolean, String> {
        val hashedPassword = hashPassword(user.password)
        user.password = hashedPassword
        return try {
            userDao.insertAll(user)
            Pair(true, success)
        } catch (e: SQLiteConstraintException) {
            Pair(false, "User already exists")
        } catch (e: SQLiteException) {
            Pair(false, "Database Error : ${e.message}")
        } catch (e: Exception) {
            Pair(false, "Unknown Error : ${e.message}")
        }
    }

    fun deleteUser(user: User): Pair<Boolean, String> {
        return try {
            userDao.delete(user)
            Pair(true, success)
        } catch (e: SQLiteConstraintException) {
            Pair(false, userDoesntExists)
        } catch (e: SQLiteException) {
            Pair(false, "Database Error : ${e.message}")
        } catch (e: Exception) {
            Pair(false, "Unknown Error : ${e.message}")
        }
    }

    fun updateUser(user: User, isPasswordChanged: Boolean = false): Pair<Boolean, String> {
        //Si le mot de passe n'est pas modifié, on ne le hash pas, sinon on le hash
        if (isPasswordChanged) {
            val hashedPassword = hashPassword(user.password)
            user.password = hashedPassword
        }
        return try {
            userDao.update(user)
            Pair(true, success)
        } catch (e: SQLiteConstraintException) {
            Pair(false, userDoesntExists)
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
            //Si l'API est inférieure à 26 (Oreo), on utilise la méthode suivante
            android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT)
        }
    }

    fun setConnected(user: User): Pair<Boolean, String> {
        return try {
            for (userCourant in userDao.getByConnected()) {
                userCourant.isConnected = false
                userDao.update(userCourant)
            }
            user.isConnected = true
            userDao.update(user)
            userDao.update(user)
            Pair(true, success)

        } catch (e: SQLiteConstraintException) {
            Pair(false, userDoesntExists)
        } catch (e: SQLiteException) {
            Pair(false, "Database Error : ${e.message}")
        } catch (e: Exception) {
            Pair(false, "Unknown Error : ${e.message}")
        }
    }

    fun isValidPassword(pass: String): Pair<Boolean, String> {
        return try {
            val res = getUsersConnected().first().password == hashPassword(pass)
            Pair(res, success)
        } catch (e: SQLiteConstraintException) {
            Pair(false, userDoesntExists)
        } catch (e: SQLiteException) {
            Pair(false, "Database Error : ${e.message}")
        } catch (e: Exception) {
            Pair(false, "Unknown Error : ${e.message}")
        }
    }

}
