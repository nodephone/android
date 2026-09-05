package com.nodephone.android.data.realtime.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "realtime_events")
data class RealtimeEventEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val eventType: String,
    val user: String = "system",
    val durationMs: Long = 0L,
    val details: String = "",
    val timestamp: Long = 0L
)
