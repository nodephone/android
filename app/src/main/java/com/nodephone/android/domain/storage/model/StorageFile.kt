package com.nodephone.android.domain.storage.model

data class StorageFile(
    val id: String,
    val bucketId: String,
    val projectId: String,
    val name: String,
    val path: String,
    val mimeType: String = "application/octet-stream",
    val sizeBytes: Long = 0L,
    val createdTimestamp: Long = System.currentTimeMillis(),
    val updatedTimestamp: Long = System.currentTimeMillis()
)
