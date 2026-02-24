package com.example.securehomeplus.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.securehomeplus.data.database.dao.ReportDao
import com.example.securehomeplus.data.database.dao.SecurityDao
import com.example.securehomeplus.data.database.dao.UserDao
import com.example.securehomeplus.data.database.entities.Report
import com.example.securehomeplus.data.database.entities.SecurityFactor
import com.example.securehomeplus.data.database.entities.User

@Database(entities = [User::class, SecurityFactor::class, Report::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun securityDao(): SecurityDao
    abstract fun reportDao(): ReportDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "secure_home_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
