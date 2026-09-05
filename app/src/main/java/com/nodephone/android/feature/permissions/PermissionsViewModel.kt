package com.nodephone.android.feature.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.nodephone.android.core.utils.BatteryOptimizationHelper
import com.nodephone.android.domain.diagnostics.model.PermissionItem
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class PermissionsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val batteryOptimizationHelper: BatteryOptimizationHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(PermissionsUiState())
    val uiState: StateFlow<PermissionsUiState> = _uiState.asStateFlow()

    init {
        checkPermissions()
    }

    fun checkPermissions() {
        val notificationGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        val storageGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            true
        } else {
            ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }

        val cameraGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        val foregroundServiceGranted = true

        val list = listOf(
            PermissionItem(
                name = "Notifications",
                description = "Delivers server health alerts and security notifications",
                permissionKey = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.POST_NOTIFICATIONS else "android.permission.POST_NOTIFICATIONS",
                isGranted = notificationGranted
            ),
            PermissionItem(
                name = "Storage & Sandbox Access",
                description = "Allows project database, file buckets, and backup archive creation",
                permissionKey = Manifest.permission.READ_EXTERNAL_STORAGE,
                isGranted = storageGranted
            ),
            PermissionItem(
                name = "Camera (QR Scanner)",
                description = "Enables camera scanner to pair with NodePhone Studio in <30 seconds",
                permissionKey = Manifest.permission.CAMERA,
                isGranted = cameraGranted
            ),
            PermissionItem(
                name = "Foreground Service",
                description = "Keeps NodePhone embedded backend process running reliably in background",
                permissionKey = "android.permission.FOREGROUND_SERVICE",
                isGranted = foregroundServiceGranted
            )
        )

        val isBatteryOptimized = !batteryOptimizationHelper.isIgnoringBatteryOptimizations()
        _uiState.update { it.copy(permissions = list, isBatteryOptimized = isBatteryOptimized) }
    }

    fun openSystemSettings() {
        batteryOptimizationHelper.openAppSystemSettings()
    }

    fun openBatterySettings() {
        batteryOptimizationHelper.openBatteryOptimizationSettings()
    }

    fun clearUserMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }
}
