package com.nodephone.android.feature.trusted

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nodephone.android.data.trusted.TrustedDeviceRepository
import com.nodephone.android.domain.trusted.model.TrustedDevice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrustedDevicesViewModel @Inject constructor(
    private val repository: TrustedDeviceRepository
) : ViewModel() {

    private val _deviceToRename = MutableStateFlow<TrustedDevice?>(null)

    val uiState: StateFlow<TrustedDevicesUiState> = combine(
        repository.trustedDevices,
        _deviceToRename
    ) { devices, deviceToRename ->
        TrustedDevicesUiState(
            devices = devices,
            deviceToRename = deviceToRename
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TrustedDevicesUiState()
    )

    fun startRename(device: TrustedDevice) {
        _deviceToRename.value = device
    }

    fun cancelRename() {
        _deviceToRename.value = null
    }

    fun submitRename(deviceId: String, newName: String) {
        viewModelScope.launch {
            if (newName.isNotBlank()) {
                repository.renameDevice(deviceId, newName.trim())
            }
            _deviceToRename.value = null
        }
    }

    fun revokeDevice(deviceId: String) {
        viewModelScope.launch {
            repository.revokeDevice(deviceId)
        }
    }
}
