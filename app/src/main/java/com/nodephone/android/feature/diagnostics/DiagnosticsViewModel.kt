package com.nodephone.android.feature.diagnostics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nodephone.android.core.utils.BatteryOptimizationHelper
import com.nodephone.android.data.diagnostics.DiagnosticsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiagnosticsViewModel @Inject constructor(
    private val diagnosticsRepository: DiagnosticsRepository,
    private val batteryOptimizationHelper: BatteryOptimizationHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(DiagnosticsUiState())
    val uiState: StateFlow<DiagnosticsUiState> = _uiState.asStateFlow()

    init {
        observeData()
        checkBatteryOptimization()
    }

    private fun observeData() {
        viewModelScope.launch {
            diagnosticsRepository.deviceOverviewFlow.collect { overview ->
                _uiState.update { it.copy(overview = overview) }
            }
        }
        viewModelScope.launch {
            diagnosticsRepository.networkDiagnosticsFlow.collect { result ->
                _uiState.update { it.copy(networkResult = result) }
            }
        }
        viewModelScope.launch {
            diagnosticsRepository.logsFlow.collect { logs ->
                _uiState.update { it.copy(logs = logs) }
            }
        }
        viewModelScope.launch {
            diagnosticsRepository.updateInfoFlow.collect { updateInfo ->
                _uiState.update { it.copy(updateInfo = updateInfo) }
            }
        }
    }

    fun checkBatteryOptimization() {
        val isOptimized = !batteryOptimizationHelper.isIgnoringBatteryOptimizations()
        _uiState.update { it.copy(isBatteryOptimized = isOptimized) }
    }

    fun openBatterySettings() {
        batteryOptimizationHelper.openBatteryOptimizationSettings()
    }

    fun selectTab(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setFilterLevel(level: String) {
        _uiState.update { it.copy(filterLevel = level) }
    }

    fun runNetworkTest() {
        viewModelScope.launch {
            _uiState.update { it.copy(isTestingNetwork = true) }
            diagnosticsRepository.runNetworkDiagnostics()
            _uiState.update { it.copy(isTestingNetwork = false, userMessage = "Network latency check completed.") }
        }
    }

    fun generateSystemReport() {
        val report = diagnosticsRepository.generateSystemReport()
        _uiState.update { it.copy(systemReport = report, userMessage = "System diagnostics report compiled.") }
    }

    fun clearLogs() {
        diagnosticsRepository.clearLogs()
        _uiState.update { it.copy(userMessage = "Logs cleared.") }
    }

    fun checkForUpdates() {
        viewModelScope.launch {
            diagnosticsRepository.checkForUpdates()
            _uiState.update { it.copy(userMessage = "Checked software updates.") }
        }
    }

    fun exportLogsJson(): String {
        return diagnosticsRepository.exportLogsJson()
    }

    fun clearUserMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }
}
