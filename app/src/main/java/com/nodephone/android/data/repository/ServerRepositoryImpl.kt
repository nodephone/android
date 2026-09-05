package com.nodephone.android.data.repository

import com.nodephone.android.core.server.NodePhoneServerManager
import com.nodephone.android.domain.model.ServerStats
import com.nodephone.android.domain.model.ServerStatus
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServerRepositoryImpl @Inject constructor(
    private val serverManager: NodePhoneServerManager
) : ServerRepository {

    override val serverStatus: StateFlow<ServerStatus> = serverManager.serverStatus
    override val serverStats: StateFlow<ServerStats> = serverManager.serverStats

    override fun startServer() = serverManager.startServer()
    override fun stopServer() = serverManager.stopServer()
    override fun restartServer() = serverManager.restartServer()
    override fun setAutoStartOnBoot(enabled: Boolean) = serverManager.setAutoStartOnBoot(enabled)
}
