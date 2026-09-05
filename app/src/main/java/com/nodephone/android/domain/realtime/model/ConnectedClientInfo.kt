package com.nodephone.android.domain.realtime.model

data class ConnectedClientInfo(
    val clientId: String,
    val clientName: String,
    val ipAddress: String,
    val connectedAt: Long,
    val activeChannelsCount: Int = 1,
    val role: String = "Admin Studio"
)
