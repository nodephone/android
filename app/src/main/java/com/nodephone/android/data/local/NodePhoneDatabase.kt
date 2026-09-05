package com.nodephone.android.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nodephone.android.data.local.dao.ServerConfigDao
import com.nodephone.android.data.local.entity.ServerConfigEntity

@Database(
    entities = [ServerConfigEntity::class],
    version = 1,
    exportSchema = false
)
abstract class NodePhoneDatabase : RoomDatabase() {
    abstract fun serverConfigDao(): ServerConfigDao
}
