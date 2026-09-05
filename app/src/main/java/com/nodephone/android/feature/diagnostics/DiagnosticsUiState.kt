package com.nodephone.android.feature.diagnostics

import com.nodephone.android.domain.diagnostics.model.AppLogEntry
import com.nodephone.android.domain.diagnostics.model.AppUpdateInfo
import com.nodephone.android.domain.diagnostics.model.DeviceOverview
import com.nodephone.android.domain.diagnostics.model.NetworkDiagnosticsResult
import com.nodephone.android.domain.diagnostics.model.SystemDiagnosticsReport

data class DiagnosticsUiState(
    val overview: DeviceOverview = DeviceOverview(),
    val networkResult: NetworkDiagnosticsResult = NetworkDiagnosticsResult(),
    val logs: List<AppLogEntry> = emptyList(),
    val updateInfo: AppUpdateInfo = AppUpdateInfo(),
    val systemReport: SystemDiagnosticsReport? = null,
    val selectedTab: Int = 0, // 0: Overview & Network, 1: Native Logs, 2: System Diagnostics Report
    val searchQuery: String = "",
    val filterLevel: String = "ALL",
    val isBatteryOptimized: Boolean = false,
    val isTestingNetwork: Boolean = false,
    val userMessage: String? = null
)
