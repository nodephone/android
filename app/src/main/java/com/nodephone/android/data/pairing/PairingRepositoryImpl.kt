package com.nodephone.android.data.pairing

import com.nodephone.android.core.mdns.MdnsBroadcaster
import com.nodephone.android.core.pairing.PairingTokenManager
import com.nodephone.android.domain.pairing.model.PairedSession
import com.nodephone.android.domain.pairing.model.PairingPayload
import com.nodephone.android.domain.pairing.model.PairingState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PairingRepositoryImpl @Inject constructor(
    private val tokenManager: PairingTokenManager,
    private val mdnsBroadcaster: MdnsBroadcaster
) : PairingRepository {

    private val scope = CoroutineScope(Dispatchers.Default)

    private val _pairingState = MutableStateFlow(PairingState.WAITING)
    override val pairingState: StateFlow<PairingState> = _pairingState.asStateFlow()

    override val currentPayload: StateFlow<PairingPayload?> = tokenManager.currentPayload
    override val remainingSeconds: StateFlow<Int> = tokenManager.remainingSeconds

    private val _activeSession = MutableStateFlow<PairedSession?>(null)
    override val activeSession: StateFlow<PairedSession?> = _activeSession.asStateFlow()

    private var durationTimerJob: Job? = null

    init {
        scope.launch {
            tokenManager.remainingSeconds.collect { secs ->
                if (secs <= 0 && _pairingState.value == PairingState.WAITING) {
                    _pairingState.value = PairingState.EXPIRED
                }
            }
        }
    }

    override fun generateNewToken(): PairingPayload {
        _pairingState.value = PairingState.WAITING
        return tokenManager.generateNewToken()
    }

    override fun invalidateToken() {
        tokenManager.invalidateToken()
        if (_pairingState.value == PairingState.WAITING) {
            _pairingState.value = PairingState.EXPIRED
        }
    }

    override fun simulatePairing(studioName: String, studioIp: String) {
        _pairingState.value = PairingState.PAIRING
        tokenManager.invalidateToken()

        scope.launch {
            delay(1200) // Pairing animation delay
            val session = PairedSession(
                sessionId = UUID.randomUUID().toString(),
                studioName = studioName,
                studioIp = studioIp,
                pairedAtTimestamp = System.currentTimeMillis()
            )
            _activeSession.value = session
            _pairingState.value = PairingState.CONNECTED
            startActiveSessionDurationTracker()
        }
    }

    override fun revokePairing() {
        durationTimerJob?.cancel()
        durationTimerJob = null
        _activeSession.value = null
        _pairingState.value = PairingState.DISCONNECTED
        generateNewToken()
    }

    override fun startMdnsBroadcasting() {
        mdnsBroadcaster.startBroadcasting(port = 8080)
    }

    override fun stopMdnsBroadcasting() {
        mdnsBroadcaster.stopBroadcasting()
    }

    private fun startActiveSessionDurationTracker() {
        durationTimerJob?.cancel()
        durationTimerJob = scope.launch {
            val startTime = System.currentTimeMillis()
            while (isActive && _pairingState.value == PairingState.CONNECTED) {
                delay(1000)
                val elapsed = (System.currentTimeMillis() - startTime) / 1000
                _activeSession.value = _activeSession.value?.copy(
                    activeSessionDurationSeconds = elapsed,
                    signalQualityPercent = 95 + (Math.random() * 5).toInt(),
                    latencyMs = 3 + (Math.random() * 4).toInt()
                )
            }
        }
    }
}
