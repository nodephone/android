package com.nodephone.android.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationEngine @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val CHANNEL_HEALTH = "server_health"
        const val CHANNEL_SECURITY = "security_alerts"
        const val CHANNEL_STORAGE = "storage_alerts"
        const val CHANNEL_ERRORS = "system_errors"
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val channels = listOf(
                NotificationChannel(
                    CHANNEL_HEALTH,
                    "Server Health & Lifecycle",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Alerts regarding NodePhone server startup, shutdown, and health status."
                },
                NotificationChannel(
                    CHANNEL_SECURITY,
                    "Security & Trust",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Notifications for device pairing requests, auth alerts, and session revocations."
                },
                NotificationChannel(
                    CHANNEL_STORAGE,
                    "Storage & Disk Usage",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Warnings when storage usage is near capacity or backup completed."
                },
                NotificationChannel(
                    CHANNEL_ERRORS,
                    "System & Realtime Errors",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Critical errors and exception notifications."
                }
            )

            channels.forEach { channel ->
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    fun sendNotification(
        channelId: String,
        title: String,
        message: String,
        notificationId: Int = System.currentTimeMillis().toInt()
    ) {
        try {
            val builder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.stat_notify_sync)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)

            val manager = NotificationManagerCompat.from(context)
            manager.notify(notificationId, builder.build())
        } catch (e: SecurityException) {
            // Permission not granted on Android 13+ if user revoked
        }
    }

    fun notifyServerStarted(url: String) {
        sendNotification(
            channelId = CHANNEL_HEALTH,
            title = "NodePhone Server Online",
            message = "Embedded server is running live at $url"
        )
    }

    fun notifyServerStopped() {
        sendNotification(
            channelId = CHANNEL_HEALTH,
            title = "NodePhone Server Stopped",
            message = "The background backend process has shut down."
        )
    }

    fun notifyPairingRequest(deviceName: String) {
        sendNotification(
            channelId = CHANNEL_SECURITY,
            title = "New Studio Pairing Request",
            message = "Studio device '$deviceName' is requesting connection."
        )
    }

    fun notifyStorageWarning(percentUsed: Int) {
        sendNotification(
            channelId = CHANNEL_STORAGE,
            title = "Storage Alert",
            message = "Device storage usage is at $percentUsed%. Clean up unused buckets or backups."
        )
    }

    fun notifySystemError(errorTitle: String, errorDetails: String) {
        sendNotification(
            channelId = CHANNEL_ERRORS,
            title = "System Alert: $errorTitle",
            message = errorDetails
        )
    }
}
