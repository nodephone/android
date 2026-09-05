package com.nodephone.android.core.server

import com.nodephone.android.domain.model.ServerState
import com.nodephone.android.domain.model.ServerStats
import com.nodephone.android.domain.model.ServerStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NodePhoneServerManager @Inject constructor() {

    private val scope = CoroutineScope(Dispatchers.Default)

    private val _serverStatus = MutableStateFlow(ServerStatus())
    val serverStatus: StateFlow<ServerStatus> = _serverStatus.asStateFlow()

    private val _serverStats = MutableStateFlow(ServerStats())
    val serverStats: StateFlow<ServerStats> = _serverStats.asStateFlow()

    private var telemetryJob: Job? = null
    private var startTimeMillis: Long = 0L

    fun startServer() {
        if (_serverStatus.value.state == ServerState.RUNNING) return

        scope.launch {
            _serverStatus.value = _serverStatus.value.copy(state = ServerState.STARTING)
            delay(1000) // Simulate initialization
            startTimeMillis = System.currentTimeMillis()
            _serverStatus.value = _serverStatus.value.copy(
                state = ServerState.RUNNING,
                lastStartedTimestamp = startTimeMillis
            )
            startTelemetryLoop()
        }
    }

    fun stopServer() {
        if (_serverStatus.value.state == ServerState.STOPPED) return

        scope.launch {
            _serverStatus.value = _serverStatus.value.copy(state = ServerState.STOPPING)
            stopTelemetryLoop()
            delay(500)
            _serverStatus.value = _serverStatus.value.copy(state = ServerState.STOPPED)
            _serverStats.value = ServerStats()
        }
    }

    fun restartServer() {
        scope.launch {
            stopServer()
            delay(1000)
            startServer()
        }
    }

    fun setAutoStartOnBoot(enabled: Boolean) {
        _serverStatus.value = _serverStatus.value.copy(autoStartOnBoot = enabled)
    }

    private fun startTelemetryLoop() {
        telemetryJob?.cancel()
        telemetryJob = scope.launch {
            while (isActive && _serverStatus.value.state == ServerState.RUNNING) {
                val elapsed = (System.currentTimeMillis() - startTimeMillis) / 1000
                _serverStats.value = ServerStats(
                    uptimeSeconds = elapsed,
                    cpuUsagePercent = (1.5f + (Math.random() * 3.0)).toFloat(),
                    memoryUsageMb = 48 + (elapsed % 12),
                    databaseSizeBytes = 1024L * 1024L * 14L + (elapsed * 2048L),
                    activeConnections = 1
                )
                delay(1000)
            }
        }
    }

    private fun stopTelemetryLoop() {
        telemetryJob?.cancel()
        telemetryJob = null
    }
}
