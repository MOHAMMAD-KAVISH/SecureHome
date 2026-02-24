// service/NotificationHelper.kt
package com.example.securehomeplus.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.securehomeplus.R
import com.example.securehomeplus.ui.dashboard.DashboardActivity

object NotificationHelper {

    private const val CHANNEL_ID = "security_tips_channel"
    private const val CHANNEL_NAME = "Daily Security Tips"
    private const val NOTIFICATION_ID = 101

    private val tips = listOf(
        "🔒 Always lock your doors before leaving home.",
        "🚨 Check camera footage regularly.",
        "💡 Keep outdoor lights on at night.",
        "📱 Use strong passwords for security devices.",
        "👀 Never share your home access code."
    )

    fun showSecurityTipNotification(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create channel if needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = "Daily tips to keep your home secure"
            channel.enableLights(true)
            channel.lightColor = Color.BLUE
            notificationManager.createNotificationChannel(channel)
        }

        // Random daily tip
        val randomTip = tips.random()

        // Intent → open app
        val intent = Intent(context, DashboardActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle("🏠 SecureHome+ Tip")
            .setContentText(randomTip)
            .setStyle(NotificationCompat.BigTextStyle().bigText(randomTip))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}
