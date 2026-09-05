package com.nodephone.android.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nodephone.android.data.repository.SettingsRepository
import com.nodephone.android.domain.model.ThemeMode
import com.nodephone.android.domain.usecase.GetServerStatusUseCase
import com.nodephone.android.domain.usecase.ManageServerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    getServerStatusUseCase: GetServerStatusUseCase,
    private val manageServerUseCase: ManageServerUseCase,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        settingsRepository.themeMode,
        settingsRepository.autoStartOnBoot,
        getServerStatusUseCase.status
    ) { themeMode, autoStart, status ->
        SettingsUiState(
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
}
