package com.nodephone.android.feature.pairing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nodephone.android.core.pairing.QrCodeGenerator
import com.nodephone.android.data.pairing.PairingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class PairingViewModel @Inject constructor(
    private val repository: PairingRepository,
    private val qrCodeGenerator: QrCodeGenerator
) : ViewModel() {

    init {
        repository.startMdnsBroadcasting()
    }

    val uiState: StateFlow<PairingUiState> = combine(
        repository.pairingState,
        repository.currentPayload,
        repository.remainingSeconds,
        repository.activeSession
    ) { state, payload, seconds, session ->
        val bitmap = payload?.let { qrCodeGenerator.generateQrBitmap(it, sizePx = 512) }
        PairingUiState(
            pairingState = state,
            payload = payload,
            remainingSeconds = seconds,
            qrBitmap = bitmap,
            activeSession = session
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PairingUiState()
    )

    fun refreshToken() {
        repository.generateNewToken()
    }

    fun simulatePairing(studioName: String = "NodePhone Studio (MacBook Pro)") {
        repository.simulatePairing(studioName = studioName, studioIp = "192.168.1.45")
    }

    fun revokePairing() {
        repository.revokePairing()
    }

    override fun onCleared() {
        super.onCleared()
        repository.stopMdnsBroadcasting()
    }
}
