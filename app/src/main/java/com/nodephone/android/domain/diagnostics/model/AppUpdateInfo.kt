package com.nodephone.android.domain.diagnostics.model

enum class UpdateStatus {
    UP_TO_DATE,
    CHECKING,
    UPDATE_AVAILABLE,
    DOWNLOADING
}

data class AppUpdateInfo(
    val currentVersion: String = "1.0.0",
    val latestVersion: String = "1.0.0",
    val releaseChannel: String = "Stable",
    val status: UpdateStatus = UpdateStatus.UP_TO_DATE,
    val releaseNotes: String = "NodePhone Server is running on latest stable build.",
    val downloadProgressPercent: Int = 0,
    val lastCheckedTimestamp: Long = System.currentTimeMillis()
)
