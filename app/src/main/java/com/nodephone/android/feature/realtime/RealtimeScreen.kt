package com.nodephone.android.feature.realtime

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nodephone.android.domain.realtime.model.ConnectedClientInfo
import com.nodephone.android.domain.realtime.model.PerformanceMetrics
import com.nodephone.android.domain.realtime.model.RealtimeEvent
import com.nodephone.android.ui.theme.EmeraldAccent
import com.nodephone.android.ui.theme.LimeAccent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RealtimeScreen(
    onNavigateBack: () -> Unit,
    viewModel: RealtimeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearUserMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "REALTIME MONITORING",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            letterSpacing = 1.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "LIVE METRICS & EVENT STREAM",
                            style = MaterialTheme.typography.labelSmall,
                            color = LimeAccent
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.triggerTestNotification() }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Push Alert Test",
                            tint = LimeAccent
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Live Header Pulse
            LivePulseBanner(metrics = uiState.metrics)

            Spacer(modifier = Modifier.height(12.dp))

            // Navigation Tabs
            TabRow(
                selectedTabIndex = uiState.selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = LimeAccent,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[uiState.selectedTab]),
                        color = LimeAccent
                    )
                }
            ) {
                Tab(
                    selected = uiState.selectedTab == 0,
                    onClick = { viewModel.selectTab(0) },
                    text = { Text("LIVE METRICS", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
                Tab(
                    selected = uiState.selectedTab == 1,
                    onClick = { viewModel.selectTab(1) },
                    text = { Text("EVENT FEED (${uiState.events.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
                Tab(
                    selected = uiState.selectedTab == 2,
                    onClick = { viewModel.selectTab(2) },
                    text = { Text("CLIENTS (${uiState.connectedClients.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (uiState.selectedTab) {
                0 -> LiveMetricsTab(metrics = uiState.metrics)
                1 -> EventFeedTab(
                    events = uiState.events,
                    filterType = uiState.filterEventType,
                    onFilterChange = { viewModel.setFilterEventType(it) },
                    onSimulate = { viewModel.simulateEvent(it) },
                    onClear = { viewModel.clearEvents() }
                )
                2 -> ConnectedClientsTab(
                    clients = uiState.connectedClients,
                    onDisconnect = { viewModel.disconnectClient(it) }
                )
            }
        }
    }
}

@Composable
fun LivePulseBanner(metrics: PerformanceMetrics) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(EmeraldAccent)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "REALTIME MONITORING ONLINE",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldAccent
                    )
                    Text(
                        text = "${metrics.messagesPerSec} msg/s • ${metrics.broadcastsPerSec} bcast/s • avg ${metrics.avgLatencyMs}ms",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            Text(
                text = "${metrics.connectedClientsCount} Client Connected",
                style = MaterialTheme.typography.labelSmall,
                color = LimeAccent,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun LiveMetricsTab(metrics: PerformanceMetrics) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                text = "HARDWARE TELEMETRY",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }

        item {
            GaugeCard(
                title = "CPU LOAD",
                value = "${metrics.cpuPercent}%",
                progress = (metrics.cpuPercent / 100f).coerceIn(0f, 1f),
                accentColor = if (metrics.cpuPercent > 10f) Color(0xFFF59E0B) else EmeraldAccent
            )
        }

        item {
            GaugeCard(
                title = "HEAP MEMORY USAGE",
                value = "${metrics.memoryUsageMb} MB",
                progress = (metrics.memoryUsageMb / 256f).coerceIn(0f, 1f),
                accentColor = LimeAccent
            )
        }

        item {
            GaugeCard(
                title = "SANDBOX STORAGE USAGE",
                value = "${metrics.storageUsageMb} MB",
                progress = (metrics.storageUsageMb / 1024f).coerceIn(0f, 1f),
                accentColor = MaterialTheme.colorScheme.primary
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricMiniBox(
                    title = "NETWORK TX",
                    value = "${metrics.networkTxKbps} KB/s",
                    modifier = Modifier.weight(1f)
                )
                MetricMiniBox(
                    title = "NETWORK RX",
                    value = "${metrics.networkRxKbps} KB/s",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricMiniBox(
                    title = "BATTERY STATUS",
                    value = "${metrics.batteryPercent}%",
                    modifier = Modifier.weight(1f)
                )
                MetricMiniBox(
                    title = "ACTIVE USERS",
                    value = "${metrics.activeUsers}",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun GaugeCard(title: String, value: String, progress: Float, accentColor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
            }
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = accentColor,
                trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
fun MetricMiniBox(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = LimeAccent
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventFeedTab(
    events: List<RealtimeEvent>,
    filterType: String,
    onFilterChange: (String) -> Unit,
    onSimulate: (String) -> Unit,
    onClear: () -> Unit
) {
    val filters = listOf("ALL", "INSERT", "UPDATE", "DELETE", "AUTH", "FUNCTION", "STORAGE")

    val filteredEvents = if (filterType == "ALL") events else events.filter { it.eventType.equals(filterType, ignoreCase = true) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Quick Action Simulator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SIMULATE LIVE EVENT",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
            IconButton(onClick = onClear) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Clear events",
                    tint = Color(0xFFEF4444)
                )
            }
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(listOf("INSERT", "UPDATE", "AUTH", "FUNCTION", "STORAGE")) { type ->
                Button(
                    onClick = { onSimulate(type) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(type, fontSize = 11.sp, color = LimeAccent)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Filter chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(filters) { category ->
                FilterChip(
                    selected = filterType == category,
                    onClick = { onFilterChange(category) },
                    label = { Text(category, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = LimeAccent,
                        selectedLabelColor = Color.Black
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (filteredEvents.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No events logged for filter '$filterType'",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredEvents) { event ->
                    EventCard(event = event)
                }
            }
        }
    }
}

@Composable
fun EventCard(event: RealtimeEvent) {
    val badgeColor = when (event.eventType.uppercase()) {
        "INSERT" -> EmeraldAccent
        "UPDATE" -> LimeAccent
        "DELETE" -> Color(0xFFEF4444)
        "AUTH" -> Color(0xFF3B82F6)
        "FUNCTION" -> Color(0xFFA855F7)
        "STORAGE" -> Color(0xFFF59E0B)
        else -> MaterialTheme.colorScheme.onSurface
    }

    val timeFormatted = SimpleDateFormat("HH:mm:ss.SSS", Locale.US).format(Date(event.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp)),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(badgeColor.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = event.eventType.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = badgeColor,
                            fontSize = 10.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "by ${event.user}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                Text(
                    text = "$timeFormatted (${event.durationMs}ms)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            Text(
                text = event.details,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun ConnectedClientsTab(
    clients: List<ConnectedClientInfo>,
    onDisconnect: (String) -> Unit
) {
    if (clients.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No active Studio client sessions connected.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(clients) { client ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = client.clientName,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(EmeraldAccent)
                                    .size(8.dp)
                            )
                        }

                        Text(
                            text = "IP Address: ${client.ipAddress} • Role: ${client.role}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )

                        Text(
                            text = "Active WebSocket Channels: ${client.activeChannelsCount}",
                            style = MaterialTheme.typography.labelSmall,
                            color = LimeAccent
                        )

                        OutlinedButton(
                            onClick = { onDisconnect(client.clientId) },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Disconnect Session")
                        }
                    }
                }
            }
        }
    }
}
