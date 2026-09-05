package com.nodephone.android.feature.backups

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nodephone.android.domain.backup.model.BackupRecord
import com.nodephone.android.ui.theme.EmeraldAccent
import com.nodephone.android.ui.theme.LimeAccent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupsScreen(
    onNavigateBack: () -> Unit,
    viewModel: BackupsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "BACKUP & RECOVERY",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.openScheduleDialog() }) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Auto Backup Schedule",
                            tint = LimeAccent
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.createBackupNow() },
                containerColor = LimeAccent,
                contentColor = Color.Black
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Create Backup Now")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Dashboard Header
            BackupMetricsCard(
                totalCount = uiState.backups.size,
                totalBytes = uiState.totalSizeBytes,
                latestDate = uiState.latestBackupDate,
                scheduleType = uiState.scheduleType
            )

            Text(
                text = "BACKUP ARCHIVES FOR PROJECT '${uiState.activeProjectId}' (${uiState.backups.size})",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )

            if (uiState.backups.isEmpty()) {
                EmptyBackupsCard(onCreate = { viewModel.createBackupNow() })
            } else {
                uiState.backups.forEach { record ->
                    BackupItemCard(
                        record = record,
                        onRestore = { viewModel.openRestoreDialog(record) },
                        onVerify = { viewModel.openVerifyModal(record) },
                        onDelete = { viewModel.deleteBackup(record) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(64.dp))
        }
    }

    // Restore Safety Snapshot Dialog
    uiState.backupToRestore?.let { record ->
        SafetyRestoreDialog(
            record = record,
            onDismiss = { viewModel.closeRestoreDialog() },
            onConfirm = { viewModel.confirmRestore(record) }
        )
    }

    // Verify Checksum Modal
    uiState.backupToVerify?.let { record ->
        VerificationModal(
            record = record,
            resultText = uiState.verificationResult ?: "",
            onDismiss = { viewModel.closeVerifyModal() }
        )
    }

    // Schedule Config Dialog
    if (uiState.isScheduleDialogOpen) {
        ScheduleConfigDialog(
            currentType = uiState.scheduleType,
            currentRetention = uiState.retentionCount,
            onDismiss = { viewModel.closeScheduleDialog() },
            onConfirm = { type, retention -> viewModel.saveScheduleConfig(type, retention) }
        )
    }
}

@Composable
fun BackupMetricsCard(
    totalCount: Int,
    totalBytes: Long,
    latestDate: String,
    scheduleType: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DISASTER RECOVERY HEALTH",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Text(
                    text = "SCHEDULE: $scheduleType",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = LimeAccent
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricTile(title = "ARCHIVES", value = "$totalCount", modifier = Modifier.weight(1f))
                MetricTile(title = "STORAGE", value = formatBytes(totalBytes), modifier = Modifier.weight(1f))
                MetricTile(title = "LATEST", value = latestDate.take(11), modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun BackupItemCard(
    record: BackupRecord,
    onRestore: () -> Unit,
    onVerify: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = EmeraldAccent
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = record.scheduleType,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldAccent
                    )
                }

                Text(
                    text = formatBytes(record.sizeBytes),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = LimeAccent
                )
            }

            Column {
                Text(
                    text = record.fileName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Created: ${formatTimestamp(record.createdTimestamp)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onRestore,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = LimeAccent, contentColor = Color.Black),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Restore", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onVerify,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Verify")
                }

                IconButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444))
                }
            }
        }
    }
}

@Composable
fun EmptyBackupsCard(onCreate: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "NO BACKUPS CREATED YET",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            Text(
                text = "Backup your database, file storage, functions, and config into a portable .npbackup archive.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Button(
                onClick = onCreate,
                colors = ButtonDefaults.buttonColors(containerColor = LimeAccent, contentColor = Color.Black)
            ) {
                Text("Create Backup Now", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SafetyRestoreDialog(
    record: BackupRecord,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Restore '${record.fileName}'?") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("A pre-restore safety snapshot will automatically be created before overwriting the active backend project.")
                Spacer(modifier = Modifier.height(4.dp))
                Text("Archive Checksum: ${record.checksum.take(16)}...", style = MaterialTheme.typography.labelMedium, color = LimeAccent)
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = LimeAccent, contentColor = Color.Black)
            ) {
                Text("Restore Project", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun VerificationModal(
    record: BackupRecord,
    resultText: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("SHA-256 Integrity Verification") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("File: ${record.fileName}", fontWeight = FontWeight.Bold)
                Text(resultText, style = MaterialTheme.typography.bodyMedium, color = EmeraldAccent)
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = LimeAccent, contentColor = Color.Black)) {
                Text("Close", fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun ScheduleConfigDialog(
    currentType: String,
    currentRetention: Int,
    onDismiss: () -> Unit,
    onConfirm: (type: String, retention: Int) -> Unit
) {
    var selectedType by remember { mutableStateOf(currentType) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Automated Backup Schedule") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Select schedule frequency:")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    OutlinedButton(onClick = { selectedType = "MANUAL" }, modifier = Modifier.weight(1f)) { Text("Manual", fontSize = 11.sp) }
                    OutlinedButton(onClick = { selectedType = "DAILY" }, modifier = Modifier.weight(1f)) { Text("Daily", fontSize = 11.sp) }
                    OutlinedButton(onClick = { selectedType = "WEEKLY" }, modifier = Modifier.weight(1f)) { Text("Weekly", fontSize = 11.sp) }
                }
                Text("Automated Retention: Keep last $currentRetention backups", style = MaterialTheme.typography.labelMedium)
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(selectedType, currentRetention) }, colors = ButtonDefaults.buttonColors(containerColor = LimeAccent, contentColor = Color.Black)) {
                Text("Save Schedule", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun MetricTile(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, style = MaterialTheme.typography.labelMedium, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = LimeAccent)
        }
    }
}

private fun formatBytes(bytes: Long): String {
    if (bytes <= 0) return "0 MB"
    val mb = bytes / (1024 * 1024)
    return if (mb > 0) "$mb MB" else "${bytes / 1024} KB"
}

private fun formatTimestamp(millis: Long): String {
    if (millis <= 0) return "Just now"
    val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.US)
    return sdf.format(Date(millis))
}
