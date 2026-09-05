package com.nodephone.android.core.server

import com.nodephone.android.core.security.SessionTokenManager
import com.nodephone.android.core.storage.StorageManager
import com.nodephone.android.data.repository.ServerRepository
import javax.inject.Inject
import javax.inject.Singleton

data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: String? = null
)

@Singleton
class StudioControlHandler @Inject constructor(
    private val sessionTokenManager: SessionTokenManager,
    private val serverRepository: ServerRepository,
    private val storageManager: StorageManager
) {
    suspend fun getStatus(sessionToken: String): ApiResponse<Map<String, Any>> {
        val device = sessionTokenManager.validateSession(sessionToken)
            ?: return ApiResponse(success = false, error = "Unauthorized: Invalid session token")

        val status = serverRepository.serverStatus.value
        val data = mapOf<String, Any>(
            "state" to status.state.name,
            "port" to status.port,
            "serverUrl" to status.serverUrl,
            "localIp" to status.localIp,
            "databaseHealth" to status.databaseHealth,
            "storageHealth" to status.storageHealth,
            "realtimeHealth" to status.realtimeHealth,
            "functionsHealth" to status.functionsHealth,
            "authorizedStudio" to device.deviceName
        )
        return ApiResponse(success = true, data = data)
    }

    suspend fun getMetrics(sessionToken: String): ApiResponse<Map<String, Any>> {
        val device = sessionTokenManager.validateSession(sessionToken)
            ?: return ApiResponse(success = false, error = "Unauthorized: Invalid session token")

        val stats = serverRepository.serverStats.value
        val data = mapOf<String, Any>(
            "uptimeSeconds" to stats.uptimeSeconds,
            "cpuUsagePercent" to stats.cpuUsagePercent,
            "memoryUsageMb" to stats.memoryUsageMb,
            "databaseSizeBytes" to stats.databaseSizeBytes,
            "storageUsageBytes" to stats.storageUsageBytes,
            "activeConnections" to stats.activeConnections
        )
        return ApiResponse(success = true, data = data)
    }

    suspend fun restartServer(sessionToken: String): ApiResponse<String> {
        sessionTokenManager.validateSession(sessionToken)
            ?: return ApiResponse(success = false, error = "Unauthorized: Invalid session token")

        serverRepository.restartServer()
        return ApiResponse(success = true, data = "Server restart initiated")
    }

    suspend fun createBackup(sessionToken: String): ApiResponse<Map<String, Any>> {
        sessionTokenManager.validateSession(sessionToken)
            ?: return ApiResponse(success = false, error = "Unauthorized: Invalid session token")

        val timestamp = System.currentTimeMillis()
        val backupName = "backup_nodephone_$timestamp.zip"
        val data = mapOf<String, Any>(
            "backupFile" to backupName,
            "createdTimestamp" to timestamp,
            "sizeBytes" to storageManager.getDatabaseSizeBytes()
        )
        return ApiResponse(success = true, data = data)
    }

    suspend fun getOpenApiSchema(sessionToken: String): ApiResponse<String> {
        sessionTokenManager.validateSession(sessionToken)
            ?: return ApiResponse(success = false, error = "Unauthorized: Invalid session token")

        val openApiJson = """
            {
              "openapi": "3.0.0",
              "info": {
                "title": "NodePhone Embedded Server API",
                "version": "1.0.0"
              },
              "paths": {
                "/health": { "get": { "summary": "Server health status" } },
                "/api/v1/auth": { "post": { "summary": "Authentication" } },
                "/api/v1/database": { "get": { "summary": "Query database" } }
              }
            }
        """.trimIndent()
        return ApiResponse(success = true, data = openApiJson)
    }

    suspend fun getLogs(sessionToken: String): ApiResponse<List<String>> {
        sessionTokenManager.validateSession(sessionToken)
            ?: return ApiResponse(success = false, error = "Unauthorized: Invalid session token")

        val logs = listOf(
            "[INFO] NodePhone Server Engine v1.0.0 started on port 8080",
            "[INFO] SQLite database initialized successfully",
            "[INFO] Storage mounted at /files/NodePhone/storage",
            "[INFO] Realtime WebSocket gateway active",
            "[INFO] Functions engine ready"
        )
        return ApiResponse(success = true, data = logs)
    }

    suspend fun getProjects(sessionToken: String): ApiResponse<List<Map<String, String>>> {
        sessionTokenManager.validateSession(sessionToken)
            ?: return ApiResponse(success = false, error = "Unauthorized: Invalid session token")

        val projects = listOf(
            mapOf("id" to "proj_default", "name" to "Default NodePhone App", "status" to "ACTIVE"),
            mapOf("id" to "proj_telemetry", "name" to "IoT Telemetry Backend", "status" to "IDLE")
        )
        return ApiResponse(success = true, data = projects)
    }
}
