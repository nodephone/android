package com.nodephone.android.data.storage.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "storage_files")
data class FileItemEntity(
    @PrimaryKey val id: String,
    val bucketId: String,
    val projectId: String,
    val name: String,
    val path: String,
    val mimeType: String = "application/octet-stream",
    val sizeBytes: Long = 0L,
    val createdTimestamp: Long = 0L,
    val updatedTimestamp: Long = 0L
)
