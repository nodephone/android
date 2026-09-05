package com.nodephone.android.domain.model

data class ServerStats(
    val uptimeSeconds: Long = 0L,
    val cpuUsagePercent: Float = 0.0f,
    val memoryUsageMb: Long = 0L,
    val databaseSizeBytes: Long = 0L,
    val activeConnections: Int = 0
)
