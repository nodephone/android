package com.nodephone.android.data.realtime.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nodephone.android.data.realtime.entity.RealtimeEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RealtimeEventDao {
    @Query("SELECT * FROM realtime_events WHERE projectId = :projectId ORDER BY timestamp DESC LIMIT 100")
    fun getEventsByProjectFlow(projectId: String): Flow<List<RealtimeEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: RealtimeEventEntity)

    @Query("DELETE FROM realtime_events WHERE projectId = :projectId")
    suspend fun clearEvents(projectId: String)
}
