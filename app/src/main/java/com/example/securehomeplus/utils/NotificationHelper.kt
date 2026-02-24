package com.example.securehomeplus.utils

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

object NotificationHelper {

    fun scheduleDailyReminder(context: Context) {
        val work = PeriodicWorkRequestBuilder<ReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(5, TimeUnit.SECONDS)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "daily_reminder",
            ExistingPeriodicWorkPolicy.UPDATE,
            work
        )
    }

    fun cancelReminder(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork("daily_reminder")
    }
}

class ReminderWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    override fun doWork(): Result {
        NotificationUtils.showNotification(applicationContext, "Daily Safety Check",
            "Don’t forget to perform your safety survey today!")
        return Result.success()
    }
}
