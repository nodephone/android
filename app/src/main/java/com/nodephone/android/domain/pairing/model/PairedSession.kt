package com.nodephone.android.domain.pairing.model

data class PairedSession(
    val sessionId: String,
    val studioName: String,
    val studioIp: String,
    val pairedAtTimestamp: Long,
    val signalQualityPercent: Int = 98,
    val latencyMs: Int = 4,
    val activeSessionDurationSeconds: Long = 0L
)
