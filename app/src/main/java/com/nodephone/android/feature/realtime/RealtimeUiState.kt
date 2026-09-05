package com.nodephone.android.feature.realtime

import com.nodephone.android.domain.realtime.model.ConnectedClientInfo
import com.nodephone.android.domain.realtime.model.PerformanceMetrics
import com.nodephone.android.domain.realtime.model.RealtimeEvent

data class RealtimeUiState(
    val metrics: PerformanceMetrics = PerformanceMetrics(),
    val events: List<RealtimeEvent> = emptyList(),
    val connectedClients: List<ConnectedClientInfo> = emptyList(),
    val selectedTab: Int = 0, // 0: Live Metrics, 1: Event Stream, 2: Connected Clients
    val filterEventType: String = "ALL",
    val isLoading: Boolean = false,
    val userMessage: String? = null
)
