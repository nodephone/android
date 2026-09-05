package com.nodephone.android.domain.diagnostics.model

data class NetworkDiagnosticsResult(
    val localIp: String = "192.168.1.100",
    val gatewayIp: String = "192.168.1.1",
    val wifiSsid: String = "Wi-Fi Network",
    val wifiConnected: Boolean = true,
    val serverPort: Int = 8080,
    val isServerReachable: Boolean = true,
    val isStudioConnected: Boolean = true,
    val latencyMs: Int = 4,
    val packetLossPercent: Float = 0.0f,
    val lastTestedTimestamp: Long = System.currentTimeMillis()
)
