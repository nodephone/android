package com.nodephone.android.core.monitoring

import com.nodephone.android.data.realtime.RealtimeRepository
import com.nodephone.android.domain.realtime.model.RealtimeEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RealtimeEventBus @Inject constructor(
    private val repository: RealtimeRepository
) {
    private val scope = CoroutineScope(Dispatchers.Default)

    private val _eventsFlow = MutableSharedFlow<RealtimeEvent>(replay = 50)
    val eventsFlow: SharedFlow<RealtimeEvent> = _eventsFlow.asSharedFlow()

    fun publishEvent(event: RealtimeEvent) {
        scope.launch {
            _eventsFlow.emit(event)
            repository.recordEvent(event)
        }
    }

    fun publishQuick(
        projectId: String,
        eventType: String,
        user: String = "system",
        durationMs: Long = 0L,
        details: String = ""
    ) {
        val event = RealtimeEvent(
            id = "evt_${System.currentTimeMillis()}_${(100..999).random()}",
            projectId = projectId,
            eventType = eventType,
            user = user,
            durationMs = durationMs,
            details = details,
            timestamp = System.currentTimeMillis()
        )
        publishEvent(event)
    }
}
