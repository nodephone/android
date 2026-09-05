package com.nodephone.android.domain.model

enum class ServerState {
    STOPPED,
    STARTING,
    RUNNING,
    STOPPING,
    ERROR
}

data class ServerStatus(
    val state: ServerState = ServerState.STOPPED,
    val port: Int = 8080,
    val autoStartOnBoot: Boolean = false,
    val lastStartedTimestamp: Long = 0L,
    val errorMessage: String? = null
)
