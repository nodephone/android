package com.nodephone.android.data.trusted.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trusted_devices")
data class TrustedDeviceEntity(
    @PrimaryKey val deviceId: String,
    val deviceName: String,
    val fingerprint: String,
    val firstPairedTimestamp: Long,
    val lastConnectedTimestamp: Long,
    val studioVersion: String = "v1.0.0",
    val connectedIp: String = "",
    val isTrusted: Boolean = true,
    val sessionToken: String = ""
)
