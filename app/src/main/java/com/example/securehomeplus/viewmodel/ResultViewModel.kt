package com.example.securehomeplus.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.securehomeplus.data.database.AppDatabase
import com.example.securehomeplus.data.database.entities.Report
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ResultViewModel(application: Application) : AndroidViewModel(application) {
    private val reportDao = AppDatabase.getDatabase(application).reportDao()

    /** Save a report asynchronously */
    fun saveReport(report: Report, onComplete: (Long) -> Unit) {
        viewModelScope.launch {
            val rowId = withContext(Dispatchers.IO) {
                try {
                    reportDao.insertReport(report)
                } catch (e: Exception) {
                    e.printStackTrace()
                    -1L
                }
            }
            onComplete(rowId)
        }
    }

    /** Fetch all saved reports for current user */
    fun getReports(email: String, onComplete: (List<Report>) -> Unit) {
        viewModelScope.launch {
            val reports = withContext(Dispatchers.IO) {
                try {
                    reportDao.getReportsForUser(email)
                } catch (e: Exception) {
                    e.printStackTrace()
                    emptyList<Report>()
                }
            }
            onComplete(reports)
        }
    }
}
