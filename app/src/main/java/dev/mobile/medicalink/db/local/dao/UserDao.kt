package dev.mobile.medicalink.db.local.dao

import androidx.room.*
import dev.mobile.medicalink.db.local.entity.User

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAll(): List<User>

    @Query("SELECT * FROM user WHERE uuid IN (:uuid)")
    fun getById(uuid: String): List<User>

    @Query("SELECT * FROM user WHERE isConnected IN (:isConnected)")
    fun getByConnected(isConnected: Boolean): List<User>

    @Insert
    fun insertAll(vararg users: User)

    @Delete
    fun delete(user: User)

    @Update
    fun update(user: User)


}
