package com.nodephone.android.data.storage.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nodephone.android.data.storage.entity.BucketEntity
import com.nodephone.android.data.storage.entity.FileItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StorageDao {
    @Query("SELECT * FROM storage_buckets WHERE projectId = :projectId ORDER BY createdTimestamp DESC")
    fun getBucketsByProjectFlow(projectId: String): Flow<List<BucketEntity>>

    @Query("SELECT * FROM storage_files WHERE bucketId = :bucketId AND projectId = :projectId ORDER BY createdTimestamp DESC")
    fun getFilesByBucketFlow(bucketId: String, projectId: String): Flow<List<FileItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateBucket(bucket: BucketEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateFile(file: FileItemEntity)

    @Query("DELETE FROM storage_buckets WHERE id = :bucketId AND projectId = :projectId")
    suspend fun deleteBucket(bucketId: String, projectId: String)

    @Query("DELETE FROM storage_files WHERE id = :fileId AND projectId = :projectId")
    suspend fun deleteFile(fileId: String, projectId: String)

    @Query("DELETE FROM storage_files WHERE bucketId = :bucketId AND projectId = :projectId")
    suspend fun deleteFilesByBucket(bucketId: String, projectId: String)
}
