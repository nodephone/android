package com.nodephone.android.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nodephone.android.data.local.entity.ServerConfigEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ServerConfigDao {
    @Query("SELECT * FROM server_config WHERE id = 1 LIMIT 1")
    fun getConfigFlow(): Flow<ServerConfigEntity?>

    @Query("SELECT * FROM server_config WHERE id = 1 LIMIT 1")
    suspend fun getConfig(): ServerConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(config: ServerConfigEntity)
}
