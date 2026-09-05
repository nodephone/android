package com.nodephone.android.data.repository

import com.nodephone.android.domain.model.ServerStats
import com.nodephone.android.domain.model.ServerStatus
import kotlinx.coroutines.flow.StateFlow

interface ServerRepository {
    val serverStatus: StateFlow<ServerStatus>
    val serverStats: StateFlow<ServerStats>

    fun startServer()
    fun stopServer()
    fun restartServer()
    fun setAutoStartOnBoot(enabled: Boolean)
}
