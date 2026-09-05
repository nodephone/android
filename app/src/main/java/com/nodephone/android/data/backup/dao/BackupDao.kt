package com.nodephone.android.data.backup.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nodephone.android.data.backup.entity.BackupEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BackupDao {
    @Query("SELECT * FROM backups WHERE projectId = :projectId ORDER BY createdTimestamp DESC")
    fun getBackupsByProjectFlow(projectId: String): Flow<List<BackupEntity>>

    @Query("SELECT * FROM backups WHERE id = :id LIMIT 1")
    suspend fun getBackupById(id: String): BackupEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(backup: BackupEntity)

    @Query("DELETE FROM backups WHERE id = :id AND projectId = :projectId")
    suspend fun deleteBackup(id: String, projectId: String)
}
