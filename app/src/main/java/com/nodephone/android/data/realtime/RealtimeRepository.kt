package com.nodephone.android.data.realtime

import com.nodephone.android.domain.realtime.model.RealtimeEvent
import kotlinx.coroutines.flow.StateFlow

interface RealtimeRepository {
    fun getEventsFlow(projectId: String): StateFlow<List<RealtimeEvent>>

    suspend fun logEvent(event: RealtimeEvent)
    suspend fun clearEvents(projectId: String)
}
