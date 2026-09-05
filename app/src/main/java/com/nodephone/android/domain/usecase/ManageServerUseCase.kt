package com.nodephone.android.domain.usecase

import com.nodephone.android.data.repository.ServerRepository
import javax.inject.Inject

class ManageServerUseCase @Inject constructor(
    private val repository: ServerRepository
) {
    fun start() = repository.startServer()
    fun stop() = repository.stopServer()
    fun restart() = repository.restartServer()
    fun setAutoStart(enabled: Boolean) = repository.setAutoStartOnBoot(enabled)
}
