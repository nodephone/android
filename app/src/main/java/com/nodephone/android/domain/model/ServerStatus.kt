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
    val localIp: String = "127.0.0.1",
    val serverUrl: String = "http://127.0.0.1:8080",
    val autoStartOnBoot: Boolean = false,
    val lastStartedTimestamp: Long = 0L,
    val databaseHealth: Boolean = false,
    val storageHealth: Boolean = false,
    val realtimeHealth: Boolean = false,
    val functionsHealth: Boolean = false,
    val errorMessage: String? = null
)
