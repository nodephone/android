package com.nodephone.android.domain.diagnostics.model

data class DeviceOverview(
    val deviceName: String = "Android Device",
    val manufacturer: String = "Android",
    val model: String = "Generic Device",
    val androidVersion: String = "14 (API 34)",
    val nodePhoneVersion: String = "1.0.0",
    val serverVersion: String = "v1.2.0-embedded",
    val ipAddress: String = "192.168.1.100",
    val batteryPercent: Int = 90,
    val isCharging: Boolean = false,
    val cpuUsagePercent: Float = 2.4f,
    val memoryUsageMb: Long = 128L,
    val memoryTotalMb: Long = 4096L,
    val storageFreeGb: Float = 32.5f,
    val storageTotalGb: Float = 128.0f,
    val uptimeSeconds: Long = 3600L
)
