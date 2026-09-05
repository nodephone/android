package com.nodephone.android.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nodephone.android.data.repository.SettingsRepository
import com.nodephone.android.domain.model.ThemeMode
import com.nodephone.android.domain.usecase.GetServerStatusUseCase
import com.nodephone.android.domain.usecase.ManageServerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    getServerStatusUseCase: GetServerStatusUseCase,
    private val manageServerUseCase: ManageServerUseCase,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _extraSettings = MutableStateFlow(
        SettingsUiState()
    )

    val uiState: StateFlow<SettingsUiState> = combine(
        settingsRepository.themeMode,
        settingsRepository.autoStartOnBoot,
        getServerStatusUseCase.status,
        _extraSettings
    ) { themeMode, autoStart, status, extra ->
        extra.copy(
            themeMode = themeMode,
            autoStartOnBoot = autoStart,
            port = status.port,
            appVersion = settingsRepository.appVersion,
            serverVersion = settingsRepository.serverVersion
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            settingsRepository.setThemeMode(mode)
        }
    }

    fun setAutoStartOnBoot(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setAutoStartOnBoot(enabled)
            manageServerUseCase.setAutoStart(enabled)
        }
    }

    fun setBackgroundRuntime(enabled: Boolean) {
        _extraSettings.update { it.copy(backgroundRuntimeEnabled = enabled) }
    }

    fun setHttpsReady(enabled: Boolean) {
        _extraSettings.update { it.copy(httpsReadyEnabled = enabled) }
    }

    fun setMdnsEnabled(enabled: Boolean) {
        _extraSettings.update { it.copy(mdnsEnabled = enabled) }
    }

    fun setRealtimeEnabled(enabled: Boolean) {
        _extraSettings.update { it.copy(realtimeEnabled = enabled) }
    }

    fun setDebugMode(enabled: Boolean) {
        _extraSettings.update { it.copy(debugModeEnabled = enabled) }
    }
}
