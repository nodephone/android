package com.nodephone.android.data.projects.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String = "",
    val databaseName: String = "nodephone.sqlite",
    val authEnabled: Boolean = true,
    val storageEnabled: Boolean = true,
    val realtimeEnabled: Boolean = true,
    val isActive: Boolean = false,
    val createdTimestamp: Long = 0L,
    val lastOpenedTimestamp: Long = 0L,
    val databaseSizeBytes: Long = 0L,
    val storageSizeBytes: Long = 0L
)
