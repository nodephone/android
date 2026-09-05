package com.nodephone.android.data.repository

import com.nodephone.android.domain.model.ThemeMode
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {
    val themeMode: StateFlow<ThemeMode>
    val autoStartOnBoot: StateFlow<Boolean>
    val appVersion: String
    val serverVersion: String

    suspend fun setThemeMode(mode: ThemeMode)
    suspend fun setAutoStartOnBoot(enabled: Boolean)
}
