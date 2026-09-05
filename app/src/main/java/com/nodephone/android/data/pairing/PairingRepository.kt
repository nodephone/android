package com.nodephone.android.data.pairing

import com.nodephone.android.domain.pairing.model.PairedSession
import com.nodephone.android.domain.pairing.model.PairingPayload
import com.nodephone.android.domain.pairing.model.PairingState
import kotlinx.coroutines.flow.StateFlow

interface PairingRepository {
    val pairingState: StateFlow<PairingState>
    val currentPayload: StateFlow<PairingPayload?>
    val remainingSeconds: StateFlow<Int>
    val activeSession: StateFlow<PairedSession?>

    fun generateNewToken(): PairingPayload
    fun invalidateToken()
    fun simulatePairing(studioName: String, studioIp: String)
    fun revokePairing()
    fun startMdnsBroadcasting()
    fun stopMdnsBroadcasting()
}
