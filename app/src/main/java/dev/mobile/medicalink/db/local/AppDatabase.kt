package dev.mobile.medicalink.db.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dev.mobile.medicalink.db.local.dao.UserDao
import dev.mobile.medicalink.db.local.entity.User

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object : SingletonHolder<AppDatabase, Context>({
        Room.databaseBuilder(it.applicationContext, AppDatabase::class.java, "ttest.db").build()
    })
}
