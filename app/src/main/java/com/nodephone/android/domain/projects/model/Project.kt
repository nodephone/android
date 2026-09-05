package com.nodephone.android.domain.projects.model

data class Project(
    val id: String,
    val name: String,
    val description: String = "",
    val databaseName: String = "nodephone.sqlite",
    val authEnabled: Boolean = true,
    val storageEnabled: Boolean = true,
    val realtimeEnabled: Boolean = true,
    val isActive: Boolean = false,
    val createdTimestamp: Long = System.currentTimeMillis(),
    val lastOpenedTimestamp: Long = System.currentTimeMillis(),
    val databaseSizeBytes: Long = 0L,
    val storageSizeBytes: Long = 0L
)
