package com.nodephone.android.core.server

import com.nodephone.android.core.monitoring.SystemMonitor
import com.nodephone.android.core.security.SessionTokenManager
import com.nodephone.android.data.realtime.RealtimeRepository
import com.nodephone.android.domain.realtime.model.ConnectedClientInfo
import com.nodephone.android.domain.realtime.model.PerformanceMetrics
import com.nodephone.android.domain.realtime.model.RealtimeEvent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudioRealtimeHandler @Inject constructor(
    private val sessionTokenManager: SessionTokenManager,
    private val systemMonitor: SystemMonitor,
    private val realtimeRepository: RealtimeRepository
) {
    suspend fun getLiveMetrics(sessionToken: String): ApiResponse<PerformanceMetrics> {
        sessionTokenManager.validateSession(sessionToken)
            ?: return ApiResponse(success = false, error = "Unauthorized")

        val metrics = systemMonitor.metrics.value
        return ApiResponse(success = true, data = metrics)
    }

    suspend fun getEventHistory(sessionToken: String, projectId: String, limit: Int = 50): ApiResponse<List<RealtimeEvent>> {
        sessionTokenManager.validateSession(sessionToken)
            ?: return ApiResponse(success = false, error = "Unauthorized")

        val events = realtimeRepository.getEventsForProjectFlow(projectId).value
        return ApiResponse(success = true, data = events.take(limit))
    }

    suspend fun getConnectedClients(sessionToken: String): ApiResponse<List<ConnectedClientInfo>> {
        sessionTokenManager.validateSession(sessionToken)
            ?: return ApiResponse(success = false, error = "Unauthorized")

        val clients = listOf(
            ConnectedClientInfo(
                clientId = "studio_client_01",
                clientName = "NodePhone Studio Desktop",
                ipAddress = "192.168.1.105",
                connectedAt = System.currentTimeMillis() - 3600000L,
                activeChannelsCount = 3,
                role = "Admin"
            )
        )
        return ApiResponse(success = true, data = clients)
    }

    suspend fun disconnectClient(sessionToken: String, clientId: String): ApiResponse<Boolean> {
        sessionTokenManager.validateSession(sessionToken)
            ?: return ApiResponse(success = false, error = "Unauthorized")

        return ApiResponse(success = true, data = true)
    }
}
