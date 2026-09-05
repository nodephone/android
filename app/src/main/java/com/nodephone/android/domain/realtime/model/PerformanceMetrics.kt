package com.nodephone.android.domain.realtime.model

data class PerformanceMetrics(
    val cpuPercent: Float = 1.8f,
    val memoryUsageMb: Long = 64L,
    val storageUsageMb: Long = 24L,
    val networkTxKbps: Float = 12.4f,
    val networkRxKbps: Float = 8.2f,
    val batteryPercent: Int = 92,
    val avgLatencyMs: Int = 4,
    val activeUsers: Int = 1,
    val connectedClientsCount: Int = 1,
    val messagesPerSec: Int = 14,
    val broadcastsPerSec: Int = 6
)
