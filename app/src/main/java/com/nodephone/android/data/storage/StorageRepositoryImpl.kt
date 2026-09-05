package com.nodephone.android.data.storage

import com.nodephone.android.data.storage.dao.StorageDao
import com.nodephone.android.data.storage.entity.BucketEntity
import com.nodephone.android.data.storage.entity.FileItemEntity
import com.nodephone.android.domain.storage.model.StorageBucket
import com.nodephone.android.domain.storage.model.StorageFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StorageRepositoryImpl @Inject constructor(
    private val dao: StorageDao
) : StorageRepository {

    private val scope = CoroutineScope(Dispatchers.IO)

    private val bucketsCache = mutableMapOf<String, MutableStateFlow<List<StorageBucket>>>()
    private val filesCache = mutableMapOf<String, MutableStateFlow<List<StorageFile>>>()

    override fun getBucketsFlow(projectId: String): StateFlow<List<StorageBucket>> {
        val flow = bucketsCache.getOrPut(projectId) {
            MutableStateFlow<List<StorageBucket>>(emptyList()).also { stateFlow ->
                scope.launch {
                    dao.getBucketsByProjectFlow(projectId).collect { list ->
                        stateFlow.value = list.map { it.toDomain() }
                    }
                }
            }
        }
        return flow.asStateFlow()
    }

    override fun getFilesFlow(bucketId: String, projectId: String): StateFlow<List<StorageFile>> {
        val cacheKey = "$projectId:$bucketId"
        val flow = filesCache.getOrPut(cacheKey) {
            MutableStateFlow<List<StorageFile>>(emptyList()).also { stateFlow ->
                scope.launch {
                    dao.getFilesByBucketFlow(bucketId, projectId).collect { list ->
                        stateFlow.value = list.map { it.toDomain() }
                    }
                }
            }
        }
        return flow.asStateFlow()
    }

    override suspend fun createBucket(projectId: String, name: String, isPublic: Boolean): StorageBucket {
        val bucketId = name.trim().lowercase().replace("\\s+".toRegex(), "-")
        val entity = BucketEntity(
            id = bucketId,
            projectId = projectId,
            name = name,
            isPublic = isPublic,
            createdTimestamp = System.currentTimeMillis(),
            itemCount = 0,
            totalSizeBytes = 0L
        )
        dao.insertOrUpdateBucket(entity)
        return entity.toDomain()
    }

    override suspend fun deleteBucket(bucketId: String, projectId: String) {
        dao.deleteFilesByBucket(bucketId, projectId)
        dao.deleteBucket(bucketId, projectId)
    }

    override suspend fun addFile(file: StorageFile) {
        dao.insertOrUpdateFile(file.toEntity())
    }

    override suspend fun deleteFile(fileId: String, projectId: String) {
        dao.deleteFile(fileId, projectId)
    }

    private fun BucketEntity.toDomain() = StorageBucket(
        id = id,
        projectId = projectId,
        name = name,
        isPublic = isPublic,
        createdTimestamp = createdTimestamp,
        itemCount = itemCount,
        totalSizeBytes = totalSizeBytes
    )

    private fun StorageFile.toEntity() = FileItemEntity(
        id = id,
        bucketId = bucketId,
        projectId = projectId,
        name = name,
        path = path,
        mimeType = mimeType,
        sizeBytes = sizeBytes,
        createdTimestamp = createdTimestamp,
        updatedTimestamp = updatedTimestamp
    )

    private fun FileItemEntity.toDomain() = StorageFile(
        id = id,
        bucketId = bucketId,
        projectId = projectId,
        name = name,
        path = path,
        mimeType = mimeType,
        sizeBytes = sizeBytes,
        createdTimestamp = createdTimestamp,
        updatedTimestamp = updatedTimestamp
    )
}
