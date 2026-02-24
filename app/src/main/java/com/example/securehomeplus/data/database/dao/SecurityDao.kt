package com.example.securehomeplus.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.securehomeplus.data.database.entities.SecurityFactor

@Dao
interface SecurityDao {
    @Query("SELECT * FROM security_factors")
    suspend fun getAll(): List<SecurityFactor>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<SecurityFactor>)

    @Query("DELETE FROM security_factors")
    suspend fun clearAll()
}
