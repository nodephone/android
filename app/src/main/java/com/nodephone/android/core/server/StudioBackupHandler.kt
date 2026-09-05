package com.nodephone.android.core.server

import com.nodephone.android.core.backup.BackupArchiveEngine
import com.nodephone.android.core.security.SessionTokenManager
import com.nodephone.android.data.backup.BackupRepository
import com.nodephone.android.domain.backup.model.BackupRecord
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudioBackupHandler @Inject constructor(
    private val sessionTokenManager: SessionTokenManager,
    private val archiveEngine: BackupArchiveEngine,
    private val backupRepository: BackupRepository
) {
    suspend fun createBackup(sessionToken: String, projectId: String): ApiResponse<BackupRecord> {
        sessionTokenManager.validateSession(sessionToken)
            ?: return ApiResponse(success = false, error = "Unauthorized")

        val record = archiveEngine.createBackup(projectId, scheduleType = "STUDIO_REMOTE")
        return ApiResponse(success = true, data = record)
    }

    suspend fun listBackups(sessionToken: String, projectId: String): ApiResponse<List<BackupRecord>> {
        sessionTokenManager.validateSession(sessionToken)
            ?: return ApiResponse(success = false, error = "Unauthorized")

        val list = backupRepository.getBackupsFlow(projectId).value
        return ApiResponse(success = true, data = list)
    }

    suspend fun restoreBackup(sessionToken: String, projectId: String, backupId: String): ApiResponse<String> {
        sessionTokenManager.validateSession(sessionToken)
            ?: return ApiResponse(success = false, error = "Unauthorized")

        val record = backupRepository.getBackupById(backupId)
            ?: return ApiResponse(success = false, error = "Backup record not found")

        val success = archiveEngine.restoreBackup(projectId, record)
        return if (success) {
            ApiResponse(success = true, data = "Backup restored successfully with safety snapshot created")
        } else {
            ApiResponse(success = false, error = "Backup restoration failed due to file corruption or invalid checksum")
        }
    }
}
