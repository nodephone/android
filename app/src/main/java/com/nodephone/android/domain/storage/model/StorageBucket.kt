package com.nodephone.android.domain.storage.model

data class StorageBucket(
    val id: String,
    val projectId: String,
    val name: String,
    val isPublic: Boolean = true,
    val createdTimestamp: Long = System.currentTimeMillis(),
    val itemCount: Int = 0,
    val totalSizeBytes: Long = 0L
)
