package com.nodephone.android.feature.backups

import com.nodephone.android.domain.backup.model.BackupRecord

data class BackupsUiState(
    val backups: List<BackupRecord> = emptyList(),
    val activeProjectId: String = "default",
    val totalSizeBytes: Long = 0L,
    val latestBackupDate: String = "None",
    val scheduleType: String = "MANUAL",
    val retentionCount: Int = 5,
    val backupToRestore: BackupRecord? = null,
    val backupToVerify: BackupRecord? = null,
    val isScheduleDialogOpen: Boolean = false,
    val verificationResult: String? = null
)
