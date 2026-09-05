package com.nodephone.android.service

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nodephone.android.core.server.NodePhoneServerManager
import com.nodephone.android.domain.model.ServerState
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ServerHealthWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val serverManager: NodePhoneServerManager
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val currentStatus = serverManager.serverStatus.value
        if (currentStatus.autoStartOnBoot && currentStatus.state == ServerState.STOPPED) {
            NodePhoneService.startService(context)
        }
        return Result.success()
    }
}
