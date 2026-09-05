package com.nodephone.android.domain.realtime.model

data class RealtimeEvent(
    val id: String,
    val projectId: String,
    val eventType: String, // Login, Logout, INSERT, UPDATE, DELETE, File Upload, Function Executed, Error
    val user: String = "system",
    val durationMs: Long = 0L,
    val details: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
