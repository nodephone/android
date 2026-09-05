package com.nodephone.android.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.nodephone.android.MainActivity
import com.nodephone.android.core.server.NodePhoneServerManager
import com.nodephone.android.domain.model.ServerState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class NodePhoneService : Service() {

    @Inject
    lateinit var serverManager: NodePhoneServerManager

    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private val CHANNEL_ID = "nodephone_server_channel"
    private val NOTIFICATION_ID = 1001

    companion object {
        const val ACTION_START = "ACTION_START_SERVER"
        const val ACTION_STOP = "ACTION_STOP_SERVER"
        const val ACTION_RESTART = "ACTION_RESTART_SERVER"

        fun startService(context: Context) {
            val intent = Intent(context, NodePhoneService::class.java).apply {
                action = ACTION_START
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, NodePhoneService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification("NodePhone Server initializing..."))

        serverManager.serverStatus.onEach { status ->
            val statusText = when (status.state) {
                ServerState.RUNNING -> "Server Active on port ${status.port}"
                ServerState.STARTING -> "Starting NodePhone Server..."
                ServerState.STOPPING -> "Stopping Server..."
                ServerState.STOPPED -> "Server Offline"
                ServerState.ERROR -> "Server Error: ${status.errorMessage ?: "Unknown"}"
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(NOTIFICATION_ID, buildNotification(statusText))
        }.launchIn(serviceScope)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                serverManager.startServer()
            }
            ACTION_STOP -> {
                serverManager.stopServer()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
            ACTION_RESTART -> {
                serverManager.restartServer()
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "NodePhone Server Status",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows real-time operational status of NodePhone Server"
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(content: String): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("NodePhone Server Runtime")
            .setContentText(content)
            .setSmallIcon(android.R.drawable.stat_notify_sync)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
}
