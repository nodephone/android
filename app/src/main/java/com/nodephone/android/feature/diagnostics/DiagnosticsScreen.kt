package com.nodephone.android.feature.diagnostics

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nodephone.android.domain.diagnostics.model.AppLogEntry
import com.nodephone.android.domain.diagnostics.model.AppUpdateInfo
import com.nodephone.android.domain.diagnostics.model.DeviceOverview
import com.nodephone.android.domain.diagnostics.model.LogLevel
import com.nodephone.android.domain.diagnostics.model.NetworkDiagnosticsResult
import com.nodephone.android.domain.diagnostics.model.SystemDiagnosticsReport
import com.nodephone.android.domain.diagnostics.model.UpdateStatus
import com.nodephone.android.ui.theme.EmeraldAccent
import com.nodephone.android.ui.theme.LimeAccent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiagnosticsScreen(
    onNavigateBack: () -> Unit,
    viewModel: DiagnosticsViewModel = hiltViewModel()
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
                            text = "DIAGNOSTICS & LOGS",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            letterSpacing = 1.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "DEVICE CONTROL CENTER",
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
            // Live Device Hero Card
            DeviceOverviewHeroCard(overview = uiState.overview)

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
                    text = { Text("OVERVIEW & NETWORK", fontWeight = FontWeight.Bold, fontSize = 11.sp) }
                )
                Tab(
                    selected = uiState.selectedTab == 1,
                    onClick = { viewModel.selectTab(1) },
                    text = { Text("LOGS (${uiState.logs.size})", fontWeight = FontWeight.Bold, fontSize = 11.sp) }
                )
                Tab(
                    selected = uiState.selectedTab == 2,
                    onClick = { viewModel.selectTab(2) },
                    text = { Text("JSON REPORT", fontWeight = FontWeight.Bold, fontSize = 11.sp) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (uiState.selectedTab) {
                0 -> OverviewAndNetworkTab(
                    overview = uiState.overview,
                    network = uiState.networkResult,
                    updateInfo = uiState.updateInfo,
                    isBatteryOptimized = uiState.isBatteryOptimized,
                    onTestNetwork = { viewModel.runNetworkTest() },
                    onOpenBatterySettings = { viewModel.openBatterySettings() },
                    onCheckUpdates = { viewModel.checkForUpdates() }
                )
                1 -> NativeLogsTab(
                    logs = uiState.logs,
                    searchQuery = uiState.searchQuery,
                    filterLevel = uiState.filterLevel,
                    onSearchChange = { viewModel.setSearchQuery(it) },
                    onFilterChange = { viewModel.setFilterLevel(it) },
                    onClearLogs = { viewModel.clearLogs() },
                    onExportJson = {
                        val json = viewModel.exportLogsJson()
                        copyToClipboard(context, "NodePhone Logs", json)
                    }
                )
                2 -> SystemReportTab(
                    report = uiState.systemReport,
                    onGenerateReport = { viewModel.generateSystemReport() },
                    onCopyReport = { r -> copyToClipboard(context, "System Diagnostics Report", r.toJson()) }
                )
            }
        }
    }
}

@Composable
fun DeviceOverviewHeroCard(overview: DeviceOverview) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = overview.deviceName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = overview.androidVersion,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(LimeAccent.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "IP: ${overview.ipAddress}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = LimeAccent
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MiniHardwareBadge(title = "RAM", value = "${overview.memoryUsageMb} MB / ${overview.memoryTotalMb} MB", modifier = Modifier.weight(1f))
                MiniHardwareBadge(title = "STORAGE", value = "${overview.storageFreeGb} GB Free", modifier = Modifier.weight(1f))
                MiniHardwareBadge(title = "BATTERY", value = "${overview.batteryPercent}%", modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun MiniHardwareBadge(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = MaterialTheme.typography.labelSmall, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            Text(value, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun OverviewAndNetworkTab(
    overview: DeviceOverview,
    network: NetworkDiagnosticsResult,
    updateInfo: AppUpdateInfo,
    isBatteryOptimized: Boolean,
    onTestNetwork: () -> Unit,
    onOpenBatterySettings: () -> Unit,
    onCheckUpdates: () -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Network Diagnostics Card
        item {
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
                            text = "NETWORK DIAGNOSTICS",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = LimeAccent
                        )
                        Button(
                            onClick = onTestNetwork,
                            colors = ButtonDefaults.buttonColors(containerColor = LimeAccent, contentColor = Color.Black),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Test Network", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Text("Local IP: ${network.localIp} • Gateway: ${network.gatewayIp}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
                    Text("Wi-Fi Interface: ${network.wifiSsid}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
                    Text("Server Port ${network.serverPort} Reachability: ${if (network.isServerReachable) "REACHABLE (OK)" else "UNREACHABLE"}", style = MaterialTheme.typography.bodyMedium, color = if (network.isServerReachable) EmeraldAccent else Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                    Text("Round-trip Latency: ${network.latencyMs} ms", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
            }
        }

        // Battery Optimization Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, if (isBatteryOptimized) Color(0xFFF59E0B) else EmeraldAccent, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isBatteryOptimized) Icons.Default.BatteryAlert else Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (isBatteryOptimized) Color(0xFFF59E0B) else EmeraldAccent,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "BATTERY OPTIMIZATION",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (isBatteryOptimized) Color(0xFFF59E0B) else EmeraldAccent
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isBatteryOptimized) "App is subject to battery restrictions. Exclude to prevent background service kills." else "Excluded from battery optimizations. Maximum server reliability.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = onOpenBatterySettings,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (isBatteryOptimized) "Exclude →" else "Settings", fontSize = 11.sp)
                    }
                }
            }
        }

        // Software Update Framework Card
        item {
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.SystemUpdate, contentDescription = null, tint = LimeAccent, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "UPDATE CENTER FRAMEWORK",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        OutlinedButton(
                            onClick = onCheckUpdates,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Check Updates", fontSize = 11.sp)
                        }
                    }

                    Text("Installed Version: v${updateInfo.currentVersion} • Channel: ${updateInfo.releaseChannel}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
                    Text(updateInfo.releaseNotes, style = MaterialTheme.typography.bodySmall, color = EmeraldAccent)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NativeLogsTab(
    logs: List<AppLogEntry>,
    searchQuery: String,
    filterLevel: String,
    onSearchChange: (String) -> Unit,
    onFilterChange: (String) -> Unit,
    onClearLogs: () -> Unit,
    onExportJson: () -> Unit
) {
    val filters = listOf("ALL", "INFO", "WARN", "ERROR", "DEBUG")

    val filteredLogs = logs.filter { entry ->
        val matchesLevel = if (filterLevel == "ALL") true else entry.level.name.equals(filterLevel, ignoreCase = true)
        val matchesSearch = if (searchQuery.isBlank()) true else {
            entry.message.contains(searchQuery, ignoreCase = true) || entry.tag.contains(searchQuery, ignoreCase = true)
        }
        matchesLevel && matchesSearch
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Search logs by tag or message...") },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = LimeAccent,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Level Filter chips & Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filters) { lvl ->
                    FilterChip(
                        selected = filterLevel == lvl,
                        onClick = { onFilterChange(lvl) },
                        label = { Text(lvl, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = LimeAccent,
                            selectedLabelColor = Color.Black
                        )
                    )
                }
            }

            Row {
                IconButton(onClick = onExportJson) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy JSON", tint = LimeAccent)
                }
                IconButton(onClick = onClearLogs) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Clear", tint = Color(0xFFEF4444))
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (filteredLogs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No log entries match the current filter.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredLogs) { entry ->
                    LogCard(entry = entry)
                }
            }
        }
    }
}

@Composable
fun LogCard(entry: AppLogEntry) {
    val levelColor = when (entry.level) {
        LogLevel.INFO -> EmeraldAccent
        LogLevel.WARN -> Color(0xFFF59E0B)
        LogLevel.ERROR -> Color(0xFFEF4444)
        LogLevel.DEBUG -> Color(0xFF3B82F6)
    }

    val timeFormatted = SimpleDateFormat("HH:mm:ss.SSS", Locale.US).format(Date(entry.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
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
                            .background(levelColor.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = entry.level.name,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = levelColor,
                            fontSize = 10.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "[${entry.tag}]",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = timeFormatted,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            Text(
                text = entry.message,
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun SystemReportTab(
    report: SystemDiagnosticsReport?,
    onGenerateReport: () -> Unit,
    onCopyReport: (SystemDiagnosticsReport) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SYSTEM DIAGNOSTICS JSON",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = LimeAccent
            )
            Button(
                onClick = onGenerateReport,
                colors = ButtonDefaults.buttonColors(containerColor = LimeAccent, contentColor = Color.Black),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Generate Report", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (report == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Tap 'Generate Report' to compile structured system JSON diagnostic report.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        } else {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Report ID: ${report.reportId}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                        IconButton(onClick = { onCopyReport(report) }) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy JSON", tint = LimeAccent)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = report.toJson(),
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                        color = LimeAccent
                    )
                }
            }
        }
    }
}

private fun copyToClipboard(context: Context, label: String, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "$label copied to clipboard", Toast.LENGTH_SHORT).show()
}
