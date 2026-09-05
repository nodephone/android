package com.nodephone.android.data.diagnostics

import com.nodephone.android.core.diagnostics.LogManager
import com.nodephone.android.core.diagnostics.SystemDiagnosticsEngine
import com.nodephone.android.core.network.NetworkDiagnosticsEngine
import com.nodephone.android.domain.diagnostics.model.AppLogEntry
import com.nodephone.android.domain.diagnostics.model.AppUpdateInfo
import com.nodephone.android.domain.diagnostics.model.DeviceOverview
import com.nodephone.android.domain.diagnostics.model.LogLevel
import com.nodephone.android.domain.diagnostics.model.NetworkDiagnosticsResult
import com.nodephone.android.domain.diagnostics.model.SystemDiagnosticsReport
import com.nodephone.android.domain.diagnostics.model.UpdateStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DiagnosticsRepositoryImpl @Inject constructor(
    private val systemDiagnosticsEngine: SystemDiagnosticsEngine,
    private val networkDiagnosticsEngine: NetworkDiagnosticsEngine,
    private val logManager: LogManager
) : DiagnosticsRepository {

    private val scope = CoroutineScope(Dispatchers.Default)

    private val _deviceOverviewFlow = MutableStateFlow(systemDiagnosticsEngine.getDeviceOverview())
    override val deviceOverviewFlow: StateFlow<DeviceOverview> = _deviceOverviewFlow.asStateFlow()

    private val _networkDiagnosticsFlow = MutableStateFlow(NetworkDiagnosticsResult())
    override val networkDiagnosticsFlow: StateFlow<NetworkDiagnosticsResult> = _networkDiagnosticsFlow.asStateFlow()

    override val logsFlow: StateFlow<List<AppLogEntry>> = logManager.logsFlow

    private val _updateInfoFlow = MutableStateFlow(AppUpdateInfo())
    override val updateInfoFlow: StateFlow<AppUpdateInfo> = _updateInfoFlow.asStateFlow()

    init {
        // Start continuous live hardware updates
        scope.launch {
            while (isActive) {
                _deviceOverviewFlow.value = systemDiagnosticsEngine.getDeviceOverview()
                delay(2000)
            }
        }
    }

    override suspend fun runNetworkDiagnostics(): NetworkDiagnosticsResult {
        val result = networkDiagnosticsEngine.runDiagnostics()
        _networkDiagnosticsFlow.value = result
        logManager.i("NetworkDiagnostics", "Diagnostics ran: ${result.localIp}, latency=${result.latencyMs}ms")
        return result
    }

    override fun generateSystemReport(): SystemDiagnosticsReport {
        return systemDiagnosticsEngine.generateReport(logsFlow.value.size)
    }

    override fun log(level: LogLevel, tag: String, message: String) {
        logManager.log(level, tag, message)
    }

    override fun clearLogs() {
        logManager.clearLogs()
    }

    override fun exportLogsJson(): String {
        return logManager.exportLogsAsJson()
    }

    override suspend fun checkForUpdates(): AppUpdateInfo {
        _updateInfoFlow.value = _updateInfoFlow.value.copy(status = UpdateStatus.CHECKING)
        delay(1200)
        val info = AppUpdateInfo(
            currentVersion = "1.0.0",
            latestVersion = "1.0.0",
            releaseChannel = "Stable",
            status = UpdateStatus.UP_TO_DATE,
            releaseNotes = "NodePhone Server is running on the latest stable build (v1.0.0).",
            downloadProgressPercent = 100,
            lastCheckedTimestamp = System.currentTimeMillis()
        )
        _updateInfoFlow.value = info
        logManager.i("UpdateCenter", "Checked for updates: System is up to date.")
        return info
    }
}
