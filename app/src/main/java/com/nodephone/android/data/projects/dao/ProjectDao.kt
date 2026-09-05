package com.nodephone.android.data.projects.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nodephone.android.data.projects.entity.ProjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY lastOpenedTimestamp DESC")
    fun getAllProjectsFlow(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE isActive = 1 LIMIT 1")
    fun getActiveProjectFlow(): Flow<ProjectEntity?>

    @Query("SELECT * FROM projects WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveProject(): ProjectEntity?

    @Query("SELECT * FROM projects WHERE id = :id LIMIT 1")
    suspend fun getProjectById(id: String): ProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(project: ProjectEntity)

    @Query("UPDATE projects SET isActive = 0")
    suspend fun clearActiveProject()

    @Query("UPDATE projects SET isActive = 1, lastOpenedTimestamp = :timestamp WHERE id = :id")
    suspend fun setActiveProject(id: String, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM projects WHERE id = :id")
    suspend fun deleteProject(id: String)
}
