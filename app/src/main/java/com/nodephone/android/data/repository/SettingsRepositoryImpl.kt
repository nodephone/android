package com.nodephone.android.data.repository

import com.nodephone.android.data.local.dao.ServerConfigDao
import com.nodephone.android.data.local.entity.ServerConfigEntity
import com.nodephone.android.domain.model.ThemeMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dao: ServerConfigDao
) : SettingsRepository {

    private val scope = CoroutineScope(Dispatchers.IO)

    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    override val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _autoStartOnBoot = MutableStateFlow(false)
    override val autoStartOnBoot: StateFlow<Boolean> = _autoStartOnBoot.asStateFlow()

    override val appVersion: String = "1.0.0"
    override val serverVersion: String = "v1.0.0-embedded"

    init {
        scope.launch {
            dao.getConfigFlow().collect { entity ->
                val current = entity ?: ServerConfigEntity()
                _themeMode.value = try {
                    ThemeMode.valueOf(current.themeMode)
                } catch (e: Exception) {
                    ThemeMode.SYSTEM
                }
                _autoStartOnBoot.value = current.autoStartOnBoot
            }
        }
    }

    override suspend fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
        val current = dao.getConfig() ?: ServerConfigEntity()
        dao.insertOrUpdate(current.copy(themeMode = mode.name))
    }

    override suspend fun setAutoStartOnBoot(enabled: Boolean) {
        _autoStartOnBoot.value = enabled
        val current = dao.getConfig() ?: ServerConfigEntity()
        dao.insertOrUpdate(current.copy(autoStartOnBoot = enabled))
    }
}
