package com.nodephone.android.domain.pairing.model

data class PairingPayload(
    val deviceId: String,
    val deviceName: String,
    val pairToken: String,
    val pairCode: String,
    val serverUrl: String,
    val port: Int,
    val expiresAt: Long,
    val serverVersion: String,
    val fingerprint: String
)
