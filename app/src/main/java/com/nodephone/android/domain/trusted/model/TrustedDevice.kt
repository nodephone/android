package com.nodephone.android.domain.trusted.model

data class TrustedDevice(
    val deviceId: String,
    val deviceName: String,
    val fingerprint: String,
    val firstPairedTimestamp: Long,
    val lastConnectedTimestamp: Long,
    val studioVersion: String = "v1.0.0",
    val connectedIp: String = "192.168.1.45",
    val isTrusted: Boolean = true,
    val sessionToken: String = ""
)
