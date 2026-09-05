package com.nodephone.android.feature.settings

import com.nodephone.android.domain.model.ThemeMode

data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val autoStartOnBoot: Boolean = false,
    val port: Int = 8080,
    val appVersion: String = "1.0.0",
    val serverVersion: String = "v1.0.0-embedded"
)
