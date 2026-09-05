package com.nodephone.android.data.storage

import com.nodephone.android.domain.storage.model.StorageBucket
import com.nodephone.android.domain.storage.model.StorageFile
import kotlinx.coroutines.flow.StateFlow

interface StorageRepository {
    fun getBucketsFlow(projectId: String): StateFlow<List<StorageBucket>>
    fun getFilesFlow(bucketId: String, projectId: String): StateFlow<List<StorageFile>>

    suspend fun createBucket(projectId: String, name: String, isPublic: Boolean): StorageBucket
    suspend fun deleteBucket(bucketId: String, projectId: String)
    suspend fun addFile(file: StorageFile)
    suspend fun deleteFile(fileId: String, projectId: String)
}
