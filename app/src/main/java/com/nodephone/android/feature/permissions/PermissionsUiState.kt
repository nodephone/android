package com.nodephone.android.feature.permissions

import com.nodephone.android.domain.diagnostics.model.PermissionItem

data class PermissionsUiState(
    val permissions: List<PermissionItem> = emptyList(),
    val isBatteryOptimized: Boolean = false,
    val userMessage: String? = null
)
