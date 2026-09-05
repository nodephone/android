package com.nodephone.android.feature.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nodephone.android.domain.model.ServerState
import com.nodephone.android.domain.usecase.GetServerStatusUseCase
import com.nodephone.android.domain.usecase.ManageServerUseCase
import com.nodephone.android.service.NodePhoneService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getServerStatusUseCase: GetServerStatusUseCase,
    private val manageServerUseCase: ManageServerUseCase
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        getServerStatusUseCase.status,
        getServerStatusUseCase.stats
    ) { status, stats ->
        HomeUiState(
            status = status,
            stats = stats
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    fun toggleServer(context: Context) {
        val currentState = uiState.value.status.state
        if (currentState == ServerState.RUNNING) {
            NodePhoneService.stopService(context)
        } else if (currentState == ServerState.STOPPED) {
            NodePhoneService.startService(context)
        }
    }

    fun restartServer(context: Context) {
        manageServerUseCase.restart()
    }
}
