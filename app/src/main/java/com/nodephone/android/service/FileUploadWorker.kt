package com.nodephone.android.service

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nodephone.android.data.storage.StorageRepository
import com.nodephone.android.domain.storage.model.StorageFile
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.UUID

@HiltWorker
class FileUploadWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val storageRepository: StorageRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val fileName = inputData.getString("fileName") ?: return Result.failure()
        val bucketId = inputData.getString("bucketId") ?: "public"
        val projectId = inputData.getString("projectId") ?: "default"
        val mimeType = inputData.getString("mimeType") ?: "application/octet-stream"
        val sizeBytes = inputData.getLong("sizeBytes", 1024L)

        val storageFile = StorageFile(
            id = UUID.randomUUID().toString(),
            bucketId = bucketId,
            projectId = projectId,
            name = fileName,
            path = "/NodePhone/projects/$projectId/storage/$bucketId/$fileName",
            mimeType = mimeType,
            sizeBytes = sizeBytes,
            createdTimestamp = System.currentTimeMillis(),
            updatedTimestamp = System.currentTimeMillis()
        )

        storageRepository.addFile(storageFile)
        return Result.success()
    }
}
