package dev.mobile.medicalink.db.local.repository

import android.database.sqlite.SQLiteConstraintException
import dev.mobile.medicalink.db.local.dao.UserDao
import dev.mobile.medicalink.db.local.entity.User

class UserRepository(private val userDao: UserDao) {

    fun insertUser(user: User) : Pair<Boolean, String> {
        return try {
            userDao.insertAll(user)
            Pair(true, "Success")
        } catch (e: SQLiteConstraintException) {
            Pair(false, "User already exists")
        } catch (e: Exception) {
            Pair(false, "Error")
        }
    }

    fun getAllUsers(): List<User> {
        return userDao.getAll()
    }
}