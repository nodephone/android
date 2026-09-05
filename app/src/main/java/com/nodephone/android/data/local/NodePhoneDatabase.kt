package com.nodephone.android.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nodephone.android.data.backup.dao.BackupDao
import com.nodephone.android.data.backup.entity.BackupEntity
import com.nodephone.android.data.local.dao.ServerConfigDao
import com.nodephone.android.data.local.entity.ServerConfigEntity
import com.nodephone.android.data.projects.dao.ProjectDao
import com.nodephone.android.data.projects.entity.ProjectEntity
import com.nodephone.android.data.realtime.dao.RealtimeEventDao
import com.nodephone.android.data.realtime.entity.RealtimeEventEntity
import com.nodephone.android.data.storage.dao.StorageDao
import com.nodephone.android.data.storage.entity.BucketEntity
import com.nodephone.android.data.storage.entity.FileItemEntity
import com.nodephone.android.data.trusted.dao.TrustedDeviceDao
import com.nodephone.android.data.trusted.entity.TrustedDeviceEntity

@Database(
    entities = [
        ServerConfigEntity::class,
        TrustedDeviceEntity::class,
        ProjectEntity::class,
        BucketEntity::class,
        FileItemEntity::class,
        BackupEntity::class,
        RealtimeEventEntity::class
    ],
    version = 6,
    exportSchema = false
)
abstract class NodePhoneDatabase : RoomDatabase() {
    abstract fun serverConfigDao(): ServerConfigDao
    abstract fun trustedDeviceDao(): TrustedDeviceDao
    abstract fun projectDao(): ProjectDao
    abstract fun storageDao(): StorageDao
    abstract fun backupDao(): BackupDao
    abstract fun realtimeEventDao(): RealtimeEventDao
}
