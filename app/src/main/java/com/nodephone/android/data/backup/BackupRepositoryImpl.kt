package com.nodephone.android.data.backup

import com.nodephone.android.data.backup.dao.BackupDao
import com.nodephone.android.data.backup.entity.BackupEntity
import com.nodephone.android.domain.backup.model.BackupRecord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupRepositoryImpl @Inject constructor(
    private val dao: BackupDao
) : BackupRepository {

    private val scope = CoroutineScope(Dispatchers.IO)
    private val backupsCache = mutableMapOf<String, MutableStateFlow<List<BackupRecord>>>()

    override fun getBackupsFlow(projectId: String): StateFlow<List<BackupRecord>> {
        val flow = backupsCache.getOrPut(projectId) {
            MutableStateFlow<List<BackupRecord>>(emptyList()).also { stateFlow ->
                scope.launch {
                    dao.getBackupsByProjectFlow(projectId).collect { list ->
                        stateFlow.value = list.map { it.toDomain() }
                    }
                }
            }
        }
        return flow.asStateFlow()
    }

    override suspend fun getBackupById(id: String): BackupRecord? {
        return dao.getBackupById(id)?.toDomain()
    }

    override suspend fun addOrUpdateBackup(backup: BackupRecord) {
        dao.insertOrUpdate(backup.toEntity())
    }

    override suspend fun deleteBackup(id: String, projectId: String) {
        dao.deleteBackup(id, projectId)
    }

    private fun BackupEntity.toDomain() = BackupRecord(
        id = id,
        projectId = projectId,
        fileName = fileName,
        filePath = filePath,
        sizeBytes = sizeBytes,
        checksum = checksum,
        createdTimestamp = createdTimestamp,
        scheduleType = scheduleType,
        isValid = isValid
    )

    private fun BackupRecord.toEntity() = BackupEntity(
        id = id,
        projectId = projectId,
        fileName = fileName,
        filePath = filePath,
        sizeBytes = sizeBytes,
        checksum = checksum,
        createdTimestamp = createdTimestamp,
        scheduleType = scheduleType,
        isValid = isValid
    )
}
