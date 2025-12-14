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
    }

    init {
        createNotificationChannel()
    }

    /**
     * Schedule a reminder at the specified time
     */
    fun scheduleReminder(time: LocalTime) {
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

        // Schedule alarm
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
            }

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}

/**
 * ReminderReceiver - BroadcastReceiver that triggers when alarm fires
 */
class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        showNotification(context)

        // Reschedule for next day
        val reminderScheduler = ReminderScheduler(context)
        val currentTime = LocalTime.now()
        reminderScheduler.scheduleReminder(currentTime)
    }

    private fun showNotification(context: Context) {
        val notification = NotificationCompat.Builder(context, "sleep_reminder_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Time to Sleep")
            .setContentText("Time to go to sleep, dear! 😴")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .build()

        val notificationManager = NotificationManagerCompat.from(context)
        try {
            notificationManager.notify(1001, notification)
        } catch (e: SecurityException) {
            // Handle permission not granted
        }
    }
}