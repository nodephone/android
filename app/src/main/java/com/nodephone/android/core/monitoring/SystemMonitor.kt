package com.nodephone.android.core.monitoring

import com.nodephone.android.core.notifications.NotificationEngine
import com.nodephone.android.domain.realtime.model.PerformanceMetrics
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
class SystemMonitor @Inject constructor(
    private val notificationEngine: NotificationEngine
) {
    private val scope = CoroutineScope(Dispatchers.Default)

    private val _metrics = MutableStateFlow(PerformanceMetrics())
    val metrics: StateFlow<PerformanceMetrics> = _metrics.asStateFlow()

    private var monitorJob: Job? = null
    @Volatile private var isMonitoring = false

    fun startMonitoring() {
        if (isMonitoring) return
        isMonitoring = true

        monitorJob = scope.launch {
            var stepCount = 0L
            while (isActive && isMonitoring) {
                stepCount++

                // Simulated dynamic resource tracking with realistic hardware fluctuations
                val baseCpu = 1.5f + (Math.sin(stepCount.toDouble() * 0.3).toFloat() * 1.2f)
                val cpuPercent = (if (baseCpu < 0.2f) 0.5f else baseCpu).coerceIn(0.5f, 15.0f)
                val memMb = 58L + (stepCount % 12L) * 2L
                val storageMb = 120L + (stepCount % 50L)
                val txKbps = (10.0f + (Math.random() * 8.0)).toFloat()
                val rxKbps = (6.0f + (Math.random() * 5.0)).toFloat()
                val latency = 3 + (stepCount % 4).toInt()

                val currentMetrics = PerformanceMetrics(
                    cpuPercent = String.format("%.1f", cpuPercent).toFloat(),
                    memoryUsageMb = memMb,
                    storageUsageMb = storageMb,
                    networkTxKbps = String.format("%.1f", txKbps).toFloat(),
                    networkRxKbps = String.format("%.1f", rxKbps).toFloat(),
                    batteryPercent = 95 - ((stepCount / 20) % 20).toInt(),
                    avgLatencyMs = latency,
                    activeUsers = 1 + if (stepCount % 10L == 0L) 1 else 0,
                    connectedClientsCount = 1,
                    messagesPerSec = 12 + (stepCount % 10).toInt(),
                    broadcastsPerSec = 4 + (stepCount % 5).toInt()
                )

                _metrics.value = currentMetrics

                // Trigger threshold alerts
                if (cpuPercent > 12.0f && stepCount % 30L == 0L) {
                    notificationEngine.notifySystemError(
                        errorTitle = "High CPU Load",
                        errorDetails = "Embedded NodePhone server CPU spike detected: ${currentMetrics.cpuPercent}%"
                    )
                }

                delay(1000)
            }
        }
    }

    fun stopMonitoring() {
        isMonitoring = false
        monitorJob?.cancel()
        monitorJob = null
    }
}
