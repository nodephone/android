package com.nodephone.android.core.server

import com.nodephone.android.domain.model.ServerStats
import com.nodephone.android.domain.model.ServerStatus
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NodePhoneServerManager @Inject constructor(
    private val engine: NodePhoneServerEngine
) {
    val serverStatus: StateFlow<ServerStatus> = engine.serverStatus
    val serverStats: StateFlow<ServerStats> = engine.serverStats

    fun startServer() {
        engine.start()
    }

    fun stopServer() {
        engine.stop()
    }

    fun restartServer() {
        engine.restart()
    }

    fun setAutoStartOnBoot(enabled: Boolean) {
        engine.setAutoStartOnBoot(enabled)
    }
}
