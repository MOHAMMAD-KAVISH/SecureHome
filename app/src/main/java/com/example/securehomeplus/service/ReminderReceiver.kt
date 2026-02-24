// service/ReminderReceiver.kt
package com.example.securehomeplus.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        // Trigger the notification
        NotificationHelper.showSecurityTipNotification(context)
    }
}
