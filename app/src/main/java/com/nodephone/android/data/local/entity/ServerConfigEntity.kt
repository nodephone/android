package com.nodephone.android.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "server_config")
data class ServerConfigEntity(
    @PrimaryKey val id: Int = 1,
    val port: Int = 8080,
    val autoStartOnBoot: Boolean = false,
    val themeMode: String = "SYSTEM",
    val lastStartedTimestamp: Long = 0L,
    val lastKnownState: String = "STOPPED"
)
