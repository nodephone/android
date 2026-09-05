package com.nodephone.android.data.trusted

import com.nodephone.android.domain.trusted.model.TrustedDevice
import kotlinx.coroutines.flow.StateFlow

interface TrustedDeviceRepository {
    val trustedDevices: StateFlow<List<TrustedDevice>>

    suspend fun getDeviceById(deviceId: String): TrustedDevice?
    suspend fun getDeviceBySessionToken(token: String): TrustedDevice?
    suspend fun addOrUpdateTrustedDevice(device: TrustedDevice)
    suspend fun renameDevice(deviceId: String, newName: String)
    suspend fun revokeDevice(deviceId: String)
}
