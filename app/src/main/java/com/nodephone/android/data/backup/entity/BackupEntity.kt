package com.nodephone.android.data.backup.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "backups")
data class BackupEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val fileName: String,
    val filePath: String,
    val sizeBytes: Long = 0L,
    val checksum: String = "",
    val createdTimestamp: Long = 0L,
    val scheduleType: String = "MANUAL",
    val isValid: Boolean = true
)
