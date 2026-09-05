package com.nodephone.android.domain.diagnostics.model

enum class LogLevel {
    INFO,
    WARN,
    ERROR,
    DEBUG
}

data class AppLogEntry(
    val id: String = "log_${System.currentTimeMillis()}_${(100..999).random()}",
    val timestamp: Long = System.currentTimeMillis(),
    val level: LogLevel = LogLevel.INFO,
    val tag: String = "System",
    val message: String,
    val stackTrace: String? = null
)
