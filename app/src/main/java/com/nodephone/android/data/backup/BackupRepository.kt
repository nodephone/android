package com.nodephone.android.data.backup

import com.nodephone.android.domain.backup.model.BackupRecord
import kotlinx.coroutines.flow.StateFlow

interface BackupRepository {
    fun getBackupsFlow(projectId: String): StateFlow<List<BackupRecord>>

    suspend fun getBackupById(id: String): BackupRecord?
    suspend fun addOrUpdateBackup(backup: BackupRecord)
    suspend fun deleteBackup(id: String, projectId: String)
}
