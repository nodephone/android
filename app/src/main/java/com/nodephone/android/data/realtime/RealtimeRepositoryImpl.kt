package com.nodephone.android.data.realtime

import com.nodephone.android.data.realtime.dao.RealtimeEventDao
import com.nodephone.android.data.realtime.entity.RealtimeEventEntity
import com.nodephone.android.domain.realtime.model.RealtimeEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RealtimeRepositoryImpl @Inject constructor(
    private val dao: RealtimeEventDao
) : RealtimeRepository {

    private val scope = CoroutineScope(Dispatchers.IO)
    private val eventsCache = mutableMapOf<String, MutableStateFlow<List<RealtimeEvent>>>()

    override fun getEventsFlow(projectId: String): StateFlow<List<RealtimeEvent>> {
        val flow = eventsCache.getOrPut(projectId) {
            MutableStateFlow<List<RealtimeEvent>>(emptyList()).also { stateFlow ->
                scope.launch {
                    dao.getEventsByProjectFlow(projectId).collect { list ->
                        stateFlow.value = list.map { it.toDomain() }
                    }
                }
            }
        }
        return flow.asStateFlow()
    }

    override suspend fun logEvent(event: RealtimeEvent) {
        dao.insertEvent(event.toEntity())
    }

    override suspend fun clearEvents(projectId: String) {
        dao.clearEvents(projectId)
    }

    private fun RealtimeEventEntity.toDomain() = RealtimeEvent(
        id = id,
        projectId = projectId,
        eventType = eventType,
        user = user,
        durationMs = durationMs,
        details = details,
        timestamp = timestamp
    )

    private fun RealtimeEvent.toEntity() = RealtimeEventEntity(
        id = id,
        projectId = projectId,
        eventType = eventType,
        user = user,
        durationMs = durationMs,
        details = details,
        timestamp = timestamp
    )
}
