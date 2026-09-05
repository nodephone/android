package com.nodephone.android.core.pairing

import com.nodephone.android.core.network.NetworkUtils
import com.nodephone.android.domain.pairing.model.PairingPayload
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PairingTokenManager @Inject constructor(
    private val identityManager: DeviceIdentityManager,
    private val networkUtils: NetworkUtils
) {
    private val scope = CoroutineScope(Dispatchers.Default)
    private val secureRandom = SecureRandom()

    private val _currentPayload = MutableStateFlow<PairingPayload?>(null)
    val currentPayload: StateFlow<PairingPayload?> = _currentPayload.asStateFlow()

    private val _remainingSeconds = MutableStateFlow(300)
    val remainingSeconds: StateFlow<Int> = _remainingSeconds.asStateFlow()

    private var countdownJob: Job? = null

    init {
        generateNewToken()
    }

    fun generateNewToken(): PairingPayload {
        countdownJob?.cancel()

        val tokenBytes = ByteArray(16)
        secureRandom.nextBytes(tokenBytes)
        val token = tokenBytes.joinToString("") { "%02x".format(it) }

        val codeNum = 100000 + secureRandom.nextInt(900000)
        val pairCode = "${codeNum / 1000} ${codeNum % 1000}"

        val expiresAt = System.currentTimeMillis() + (300 * 1000L)
        val serverUrl = networkUtils.getServerUrl(port = 8080)

        val payload = PairingPayload(
            deviceId = identityManager.deviceId,
            deviceName = identityManager.deviceName,
            pairToken = token,
            pairCode = pairCode,
            serverUrl = serverUrl,
            port = 8080,
            expiresAt = expiresAt,
            serverVersion = identityManager.nodePhoneVersion,
            fingerprint = identityManager.fingerprint
        )

        _currentPayload.value = payload
        _remainingSeconds.value = 300

        startCountdownTimer()
        return payload
    }

    fun invalidateToken() {
        countdownJob?.cancel()
        _currentPayload.value = null
        _remainingSeconds.value = 0
    }

    private fun startCountdownTimer() {
        countdownJob = scope.launch {
            while (isActive && _remainingSeconds.value > 0) {
                delay(1000)
                _remainingSeconds.value -= 1
            }
            if (_remainingSeconds.value <= 0) {
                _currentPayload.value = null
            }
        }
    }
}
