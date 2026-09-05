package com.nodephone.android.data.storage.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "storage_buckets")
data class BucketEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val name: String,
    val isPublic: Boolean = true,
    val createdTimestamp: Long = 0L,
    val itemCount: Int = 0,
    val totalSizeBytes: Long = 0L
)
