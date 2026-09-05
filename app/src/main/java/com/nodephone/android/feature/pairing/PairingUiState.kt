package com.nodephone.android.feature.pairing

import android.graphics.Bitmap
import com.nodephone.android.domain.pairing.model.PairedSession
import com.nodephone.android.domain.pairing.model.PairingPayload
import com.nodephone.android.domain.pairing.model.PairingState

data class PairingUiState(
    val pairingState: PairingState = PairingState.WAITING,
    val payload: PairingPayload? = null,
    val remainingSeconds: Int = 300,
    val qrBitmap: Bitmap? = null,
    val activeSession: PairedSession? = null
)
