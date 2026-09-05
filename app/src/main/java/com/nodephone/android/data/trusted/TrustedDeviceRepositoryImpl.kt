package com.nodephone.android.data.trusted

import com.nodephone.android.data.trusted.dao.TrustedDeviceDao
import com.nodephone.android.data.trusted.entity.TrustedDeviceEntity
import com.nodephone.android.domain.trusted.model.TrustedDevice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrustedDeviceRepositoryImpl @Inject constructor(
    private val dao: TrustedDeviceDao
) : TrustedDeviceRepository {

    private val scope = CoroutineScope(Dispatchers.IO)

    private val _trustedDevices = MutableStateFlow<List<TrustedDevice>>(emptyList())
    override val trustedDevices: StateFlow<List<TrustedDevice>> = _trustedDevices.asStateFlow()

    init {
        scope.launch {
            dao.getAllTrustedDevicesFlow().collect { list ->
                _trustedDevices.value = list.map { it.toDomainModel() }
            }
        }
    }

    override suspend fun getDeviceById(deviceId: String): TrustedDevice? {
        return dao.getDeviceById(deviceId)?.toDomainModel()
    }

    override suspend fun getDeviceBySessionToken(token: String): TrustedDevice? {
        return dao.getDeviceBySessionToken(token)?.toDomainModel()
    }

    override suspend fun addOrUpdateTrustedDevice(device: TrustedDevice) {
        dao.insertOrUpdate(device.toEntity())
    }

    override suspend fun renameDevice(deviceId: String, newName: String) {
        dao.renameDevice(deviceId, newName)
    }

    override suspend fun revokeDevice(deviceId: String) {
        dao.deleteDevice(deviceId)
    }

    private fun TrustedDeviceEntity.toDomainModel(): TrustedDevice {
        return TrustedDevice(
            deviceId = deviceId,
            deviceName = deviceName,
            fingerprint = fingerprint,
            firstPairedTimestamp = firstPairedTimestamp,
            lastConnectedTimestamp = lastConnectedTimestamp,
            studioVersion = studioVersion,
            connectedIp = connectedIp,
            isTrusted = isTrusted,
            sessionToken = sessionToken
        )
    }

    private fun TrustedDevice.toEntity(): TrustedDeviceEntity {
        return TrustedDeviceEntity(
            deviceId = deviceId,
            deviceName = deviceName,
            fingerprint = fingerprint,
            firstPairedTimestamp = firstPairedTimestamp,
            lastConnectedTimestamp = lastConnectedTimestamp,
            studioVersion = studioVersion,
            connectedIp = connectedIp,
            isTrusted = isTrusted,
            sessionToken = sessionToken
        )
    }
}
