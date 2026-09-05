package com.nodephone.android.domain.backup.model

data class BackupRecord(
    val id: String,
    val projectId: String,
    val fileName: String,
    val filePath: String,
    val sizeBytes: Long = 0L,
    val checksum: String = "",
    val createdTimestamp: Long = System.currentTimeMillis(),
    val scheduleType: String = "MANUAL",
    val isValid: Boolean = true
)
