package com.nodephone.android.data.trusted.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nodephone.android.data.trusted.entity.TrustedDeviceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrustedDeviceDao {
    @Query("SELECT * FROM trusted_devices WHERE isTrusted = 1 ORDER BY lastConnectedTimestamp DESC")
    fun getAllTrustedDevicesFlow(): Flow<List<TrustedDeviceEntity>>

    @Query("SELECT * FROM trusted_devices WHERE deviceId = :deviceId LIMIT 1")
    suspend fun getDeviceById(deviceId: String): TrustedDeviceEntity?

    @Query("SELECT * FROM trusted_devices WHERE sessionToken = :token AND isTrusted = 1 LIMIT 1")
    suspend fun getDeviceBySessionToken(token: String): TrustedDeviceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(device: TrustedDeviceEntity)

    @Query("DELETE FROM trusted_devices WHERE deviceId = :deviceId")
    suspend fun deleteDevice(deviceId: String)

    @Query("UPDATE trusted_devices SET deviceName = :newName WHERE deviceId = :deviceId")
    suspend fun renameDevice(deviceId: String, newName: String)
}
