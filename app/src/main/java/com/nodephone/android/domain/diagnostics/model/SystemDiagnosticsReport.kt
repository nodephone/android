package com.nodephone.android.domain.diagnostics.model

data class SystemDiagnosticsReport(
    val reportId: String = "diag_${System.currentTimeMillis()}",
    val timestamp: Long = System.currentTimeMillis(),
    val deviceName: String = "Android Device",
    val osVersion: String = "Android 14 (API 34)",
    val cpuAbi: String = "arm64-v8a",
    val ramTotalMb: Long = 4096L,
    val ramAvailableMb: Long = 2100L,
    val storageAvailableGb: Float = 32.5f,
    val serverStatus: String = "RUNNING",
    val activeProjectsCount: Int = 1,
    val databaseSizeBytes: Long = 16777216L,
    val storageUsageBytes: Long = 8388608L,
    val activeStudioClientsCount: Int = 1,
    val logsCount: Int = 42
) {
    fun toJson(): String {
        return """
        {
          "reportId": "$reportId",
          "timestamp": $timestamp,
          "device": {
            "name": "$deviceName",
            "os": "$osVersion",
            "arch": "$cpuAbi"
          },
          "memory": {
            "totalMb": $ramTotalMb,
            "availableMb": $ramAvailableMb
          },
          "storage": {
            "availableGb": $storageAvailableGb,
            "databaseSizeBytes": $databaseSizeBytes,
            "storageUsageBytes": $storageUsageBytes
          },
          "nodephone": {
            "serverStatus": "$serverStatus",
            "activeProjectsCount": $activeProjectsCount,
            "activeStudioClientsCount": $activeStudioClientsCount,
            "logsCount": $logsCount
          }
        }
        """.trimIndent()
    }
}
