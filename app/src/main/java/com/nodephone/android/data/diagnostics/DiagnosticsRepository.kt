package com.nodephone.android.data.diagnostics

import com.nodephone.android.domain.diagnostics.model.AppLogEntry
import com.nodephone.android.domain.diagnostics.model.AppUpdateInfo
import com.nodephone.android.domain.diagnostics.model.DeviceOverview
import com.nodephone.android.domain.diagnostics.model.LogLevel
import com.nodephone.android.domain.diagnostics.model.NetworkDiagnosticsResult
import com.nodephone.android.domain.diagnostics.model.SystemDiagnosticsReport
import kotlinx.coroutines.flow.StateFlow

interface DiagnosticsRepository {
    val deviceOverviewFlow: StateFlow<DeviceOverview>
    val networkDiagnosticsFlow: StateFlow<NetworkDiagnosticsResult>
    val logsFlow: StateFlow<List<AppLogEntry>>
    val updateInfoFlow: StateFlow<AppUpdateInfo>

    suspend fun runNetworkDiagnostics(): NetworkDiagnosticsResult
    fun generateSystemReport(): SystemDiagnosticsReport
    fun log(level: LogLevel, tag: String, message: String)
    fun clearLogs()
    fun exportLogsJson(): String
    suspend fun checkForUpdates(): AppUpdateInfo
}
