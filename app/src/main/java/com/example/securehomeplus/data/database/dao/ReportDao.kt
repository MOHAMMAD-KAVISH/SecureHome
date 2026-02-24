package com.example.securehomeplus.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.securehomeplus.data.database.entities.Report

@Dao
interface ReportDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: Report): Long

    @Query("SELECT * FROM reports WHERE userEmail = :email ORDER BY dateMillis DESC")
    suspend fun getReportsForUser(email: String): List<Report>

    @Query("DELETE FROM reports WHERE id = :id")
    suspend fun deleteReportById(id: Int)

    @Query("DELETE FROM reports")
    suspend fun deleteAll()
}
