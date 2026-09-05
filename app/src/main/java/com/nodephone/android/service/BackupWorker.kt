package com.nodephone.android.service

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nodephone.android.core.backup.BackupArchiveEngine
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class BackupWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val archiveEngine: BackupArchiveEngine
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val projectId = inputData.getString("projectId") ?: "default"
        val scheduleType = inputData.getString("scheduleType") ?: "SCHEDULED"

        return try {
            archiveEngine.createBackup(projectId, scheduleType)
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
