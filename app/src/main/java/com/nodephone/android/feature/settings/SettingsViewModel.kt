package com.nodephone.android.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nodephone.android.domain.model.ServerStatus
import com.nodephone.android.domain.usecase.GetServerStatusUseCase
import com.nodephone.android.domain.usecase.ManageServerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    getServerStatusUseCase: GetServerStatusUseCase,
    private val manageServerUseCase: ManageServerUseCase
) : ViewModel() {

    val serverStatus: StateFlow<ServerStatus> = getServerStatusUseCase.status
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ServerStatus()
        )

    fun setAutoStartOnBoot(enabled: Boolean) {
        manageServerUseCase.setAutoStart(enabled)
    }
}
