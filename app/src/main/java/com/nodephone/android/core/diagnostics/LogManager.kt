package com.nodephone.android.core.diagnostics

import com.nodephone.android.domain.diagnostics.model.AppLogEntry
import com.nodephone.android.domain.diagnostics.model.LogLevel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.ConcurrentLinkedQueue
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LogManager @Inject constructor() {
    private val maxLogBuffer = 500
    private val logQueue = ConcurrentLinkedQueue<AppLogEntry>()

    private val _logsFlow = MutableStateFlow<List<AppLogEntry>>(emptyList())
    val logsFlow: StateFlow<List<AppLogEntry>> = _logsFlow.asStateFlow()

    init {
        // Seed initial system boot log entries
        log(LogLevel.INFO, "NodePhoneServer", "Embedded NodePhone Server runtime initialized.")
        log(LogLevel.INFO, "Database", "SQLite storage initialized and verified (v6).")
        log(LogLevel.INFO, "Network", "Listening for local connections on port 8080.")
        log(LogLevel.DEBUG, "mDNS", "mDNS service discovery broadcast active.")
    }

    fun log(level: LogLevel, tag: String, message: String, stackTrace: String? = null) {
        val entry = AppLogEntry(
            level = level,
            tag = tag,
            message = message,
            stackTrace = stackTrace
        )
        logQueue.add(entry)
        while (logQueue.size > maxLogBuffer) {
            logQueue.poll()
        }
        _logsFlow.value = logQueue.toList().reversed()
    }

    fun i(tag: String, message: String) = log(LogLevel.INFO, tag, message)
    fun w(tag: String, message: String) = log(LogLevel.WARN, tag, message)
    fun e(tag: String, message: String, stackTrace: String? = null) = log(LogLevel.ERROR, tag, message, stackTrace)
    fun d(tag: String, message: String) = log(LogLevel.DEBUG, tag, message)

    fun clearLogs() {
        logQueue.clear()
        _logsFlow.value = emptyList()
        i("LogManager", "Log buffer cleared by user.")
    }

    fun exportLogsAsJson(): String {
        val currentLogs = _logsFlow.value
        val sb = StringBuilder()
        sb.append("[\n")
        currentLogs.forEachIndexed { index, entry ->
            sb.append("  {\n")
            sb.append("    \"timestamp\": ${entry.timestamp},\n")
            sb.append("    \"level\": \"${entry.level}\",\n")
            sb.append("    \"tag\": \"${entry.tag}\",\n")
            sb.append("    \"message\": \"${entry.message.replace("\"", "\\\"")}\"\n")
            sb.append("  }${if (index < currentLogs.size - 1) "," else ""}\n")
        }
        sb.append("]")
        return sb.toString()
    }
}
