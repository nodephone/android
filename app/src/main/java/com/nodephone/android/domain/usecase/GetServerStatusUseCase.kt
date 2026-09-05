package com.nodephone.android.domain.usecase

import com.nodephone.android.data.repository.ServerRepository
import com.nodephone.android.domain.model.ServerStats
import com.nodephone.android.domain.model.ServerStatus
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetServerStatusUseCase @Inject constructor(
    private val repository: ServerRepository
) {
    val status: StateFlow<ServerStatus> = repository.serverStatus
    val stats: StateFlow<ServerStats> = repository.serverStats
}
