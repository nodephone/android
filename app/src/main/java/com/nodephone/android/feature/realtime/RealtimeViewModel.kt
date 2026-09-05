package com.nodephone.android.feature.realtime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nodephone.android.core.monitoring.RealtimeEventBus
import com.nodephone.android.core.monitoring.SystemMonitor
import com.nodephone.android.core.notifications.NotificationEngine
import com.nodephone.android.data.realtime.RealtimeRepository
import com.nodephone.android.domain.realtime.model.ConnectedClientInfo
import com.nodephone.android.domain.realtime.model.RealtimeEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RealtimeViewModel @Inject constructor(
    private val systemMonitor: SystemMonitor,
    private val realtimeEventBus: RealtimeEventBus,
    private val realtimeRepository: RealtimeRepository,
    private val notificationEngine: NotificationEngine
) : ViewModel() {

    private val _uiState = MutableStateFlow(RealtimeUiState())
    val uiState: StateFlow<RealtimeUiState> = _uiState.asStateFlow()

    init {
        systemMonitor.startMonitoring()
        observeSystemMetrics()
        observeLiveEvents()
        loadInitialData()
    }

    private fun observeSystemMetrics() {
        viewModelScope.launch {
            systemMonitor.metrics.collect { metrics ->
                _uiState.update { it.copy(metrics = metrics) }
            }
        }
    }

    private fun observeLiveEvents() {
        viewModelScope.launch {
            realtimeEventBus.eventsFlow.collect { event ->
                _uiState.update { current ->
                    val updated = listOf(event) + current.events
                    current.copy(events = updated.take(100))
                }
            }
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Seed connected client info
            val defaultClients = listOf(
                ConnectedClientInfo(
                    clientId = "studio_client_desktop",
                    clientName = "NodePhone Studio Desktop (macOS)",
                    ipAddress = "192.168.1.105",
                    connectedAt = System.currentTimeMillis() - 1800000L,
                    activeChannelsCount = 4,
                    role = "Primary Studio Admin"
                )
            )

            // Seed initial database events if flow is empty
            val initialEvents = realtimeRepository.getEventsForProjectFlow("proj_default").value
            if (initialEvents.isEmpty()) {
                val seedList = listOf(
                    RealtimeEvent(
                        id = "evt_init_1",
                        projectId = "proj_default",
                        eventType = "INSERT",
                        user = "client_sdk",
                        durationMs = 4L,
                        details = "Inserted 1 record into 'users' table",
                        timestamp = System.currentTimeMillis() - 60000L
                    ),
                    RealtimeEvent(
                        id = "evt_init_2",
                        projectId = "proj_default",
                        eventType = "FUNCTION",
                        user = "studio_admin",
                        durationMs = 18L,
                        details = "Executed function 'send-welcome-email'",
                        timestamp = System.currentTimeMillis() - 120000L
                    ),
                    RealtimeEvent(
                        id = "evt_init_3",
                        projectId = "proj_default",
                        eventType = "STORAGE",
                        user = "client_sdk",
                        durationMs = 45L,
                        details = "Uploaded 'avatar.png' (240 KB) to bucket 'avatars'",
                        timestamp = System.currentTimeMillis() - 240000L
                    )
                )
                seedList.forEach { realtimeRepository.recordEvent(it) }
                _uiState.update { it.copy(events = seedList, connectedClients = defaultClients, isLoading = false) }
            } else {
                _uiState.update { it.copy(events = initialEvents, connectedClients = defaultClients, isLoading = false) }
            }
        }
    }

    fun selectTab(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }

    fun setFilterEventType(type: String) {
        _uiState.update { it.copy(filterEventType = type) }
    }

    fun simulateEvent(eventType: String) {
        val details = when (eventType) {
            "INSERT" -> "Inserted row into table 'orders' (id: ${System.currentTimeMillis() % 10000})"
            "UPDATE" -> "Updated table 'settings' key 'theme' -> 'dark'"
            "DELETE" -> "Deleted record from 'logs' table"
            "AUTH" -> "User authenticated via JWT (user_id: usr_9942)"
            "FUNCTION" -> "Triggered serverless edge function 'calculate-tax'"
            "STORAGE" -> "Saved file 'report.pdf' to bucket 'documents'"
            else -> "System metric ping executed"
        }

        realtimeEventBus.publishQuick(
            projectId = "proj_default",
            eventType = eventType,
            user = if (eventType == "AUTH") "usr_9942" else "studio_client",
            durationMs = (2..28).random().toLong(),
            details = details
        )
    }

    fun clearEvents() {
        viewModelScope.launch {
            realtimeRepository.clearEvents("proj_default")
            _uiState.update { it.copy(events = emptyList()) }
        }
    }

    fun triggerTestNotification() {
        notificationEngine.sendNotification(
            channelId = NotificationEngine.CHANNEL_HEALTH,
            title = "Realtime Push Test",
            message = "Live event stream monitor is active on NodePhone Android."
        )
        _uiState.update { it.copy(userMessage = "Test notification sent") }
    }

    fun disconnectClient(clientId: String) {
        _uiState.update { current ->
            val updated = current.connectedClients.filterNot { it.clientId == clientId }
            current.copy(connectedClients = updated, userMessage = "Client disconnected")
        }
    }

    fun clearUserMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }

    override fun onCleared() {
        super.onCleared()
        systemMonitor.stopMonitoring()
    }
}
