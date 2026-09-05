package com.nodephone.android.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nodephone.android.data.local.dao.ServerConfigDao
import com.nodephone.android.data.local.entity.ServerConfigEntity
import com.nodephone.android.data.trusted.dao.TrustedDeviceDao
import com.nodephone.android.data.trusted.entity.TrustedDeviceEntity

@Database(
    entities = [ServerConfigEntity::class, TrustedDeviceEntity::class],
    version = 2,
    exportSchema = false
)
abstract class NodePhoneDatabase : RoomDatabase() {
    abstract fun serverConfigDao(): ServerConfigDao
    abstract fun trustedDeviceDao(): TrustedDeviceDao
}
