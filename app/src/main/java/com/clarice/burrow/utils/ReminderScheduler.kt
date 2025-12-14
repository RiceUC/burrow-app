package com.clarice.burrow.utils

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.clarice.burrow.R
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.*

/**
 * ReminderScheduler - Handles scheduling and cancellation of sleep reminders
 */
class ReminderScheduler(private val context: Context) {

    companion object {
        private const val CHANNEL_ID = "sleep_reminder_channel"
        private const val CHANNEL_NAME = "Sleep Reminders"
        private const val NOTIFICATION_ID = 1001
        private const val REQUEST_CODE = 1001
        private const val PREFS_NAME = "burrow_reminder_prefs"
        private const val KEY_REMINDER_HOUR = "reminder_hour"
        private const val KEY_REMINDER_MINUTE = "reminder_minute"
    }

    init {
        createNotificationChannel()
    }

    /**
     * Schedule a reminder at the specified time
     */
    fun scheduleReminder(time: LocalTime) {
        // Save the reminder time to preferences
        saveReminderTime(time)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Calculate trigger time
        val now = LocalDateTime.now()
        var reminderTime = now.toLocalDate().atTime(time)

        // If time has passed today, schedule for tomorrow
        if (reminderTime.isBefore(now)) {
            reminderTime = reminderTime.plusDays(1)
        }

        val triggerAtMillis = reminderTime
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        android.util.Log.d("ReminderScheduler", "Scheduling reminder for: $reminderTime")

        // Schedule repeating alarm
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // For Android 6.0+, use setExactAndAllowWhileIdle for one-time alarm
                // We'll reschedule in the receiver for daily repeat
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }
            android.util.Log.d("ReminderScheduler", "Reminder scheduled successfully")
        } catch (e: SecurityException) {
            android.util.Log.e("ReminderScheduler", "Failed to schedule alarm: ${e.message}")
        }
    }

    /**
     * Cancel scheduled reminder
     */
    fun cancelReminder() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()

        android.util.Log.d("ReminderScheduler", "Reminder cancelled")
    }

    /**
     * Create notification channel (Android 8.0+)
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for sleep reminders"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500)
            }

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Save reminder time to shared preferences
     */
    private fun saveReminderTime(time: LocalTime) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().apply {
            putInt(KEY_REMINDER_HOUR, time.hour)
            putInt(KEY_REMINDER_MINUTE, time.minute)
            apply()
        }
    }

    /**
     * Load reminder time from shared preferences
     */
    fun loadReminderTime(): LocalTime? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val hour = prefs.getInt(KEY_REMINDER_HOUR, -1)
        val minute = prefs.getInt(KEY_REMINDER_MINUTE, -1)

        return if (hour != -1 && minute != -1) {
            LocalTime.of(hour, minute)
        } else {
            null
        }
    }
}

/**
 * ReminderReceiver - BroadcastReceiver that triggers when alarm fires
 */
class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        android.util.Log.d("ReminderReceiver", "Reminder alarm fired")

        showNotification(context)

        // Reschedule for next day
        val reminderScheduler = ReminderScheduler(context)
        val savedTime = reminderScheduler.loadReminderTime()

        if (savedTime != null) {
            reminderScheduler.scheduleReminder(savedTime)
            android.util.Log.d("ReminderReceiver", "Rescheduled reminder for tomorrow at $savedTime")
        }
    }

    private fun showNotification(context: Context) {
        val notification = NotificationCompat.Builder(context, "sleep_reminder_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Time to Sleep 🌙")
            .setContentText("Time to go to sleep, dear! 😴")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .build()

        try {
            val notificationManager = NotificationManagerCompat.from(context)

            // Check for notification permission (Android 13+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    notificationManager.notify(1001, notification)
                    android.util.Log.d("ReminderReceiver", "Notification sent")
                }
            } else {
                notificationManager.notify(1001, notification)
                android.util.Log.d("ReminderReceiver", "Notification sent")
            }
        } catch (e: SecurityException) {
            android.util.Log.e("ReminderReceiver", "Failed to show notification: ${e.message}")
        }
    }
}