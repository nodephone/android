package com.nodephone.android.feature.storage

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.VideoFile
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nodephone.android.core.storage.SignedUrlExpiry
import com.nodephone.android.domain.storage.model.StorageBucket
import com.nodephone.android.domain.storage.model.StorageFile
import com.nodephone.android.ui.theme.EmeraldAccent
import com.nodephone.android.ui.theme.LimeAccent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StorageScreen(
    onNavigateBack: () -> Unit,
    viewModel: StorageViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "STORAGE MANAGER",
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
                    IconButton(onClick = { viewModel.toggleViewMode() }) {
                        Icon(
                            imageVector = if (uiState.isGridView) Icons.Default.List else Icons.Default.GridView,
                            contentDescription = "Toggle Grid View",
                            tint = MaterialTheme.colorScheme.onBackground
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
                onClick = { viewModel.uploadSampleFile("avatar_${System.currentTimeMillis() % 1000}.png", "image/png") },
                containerColor = LimeAccent,
                contentColor = Color.Black
            ) {
                Icon(imageVector = Icons.Default.UploadFile, contentDescription = "Import File")
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
            // Metrics Header
            StorageMetricsCard(
                usedBytes = uiState.totalUsedBytes,
                availableBytes = uiState.availableDeviceBytes,
                bucketCount = uiState.buckets.size,
                fileCount = uiState.files.size
            )

            // Buckets Selector Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "BUCKETS (${uiState.buckets.size})",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )

                TextButton(onClick = { viewModel.openCreateBucketDialog() }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = LimeAccent)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Bucket", color = LimeAccent, fontWeight = FontWeight.Bold)
                }
            }

            BucketSelectorRow(
                buckets = uiState.buckets,
                selectedBucket = uiState.selectedBucket,
                onSelect = { viewModel.selectBucket(it) }
            )

            // Search Bar
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                placeholder = { Text("Search files in current bucket...") },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // File Browser Header
            Text(
                text = "FILES IN BUCKET '${uiState.selectedBucket?.name ?: "public"}' (${uiState.files.size})",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )

            if (uiState.files.isEmpty()) {
                EmptyFilesCard(onUpload = { viewModel.uploadSampleFile("document_${System.currentTimeMillis() % 1000}.pdf", "application/pdf") })
            } else {
                uiState.files.forEach { file ->
                    StorageFileItemCard(
                        file = file,
                        onPreview = { viewModel.selectFileForPreview(file) },
                        onGetSignedUrl = { viewModel.openSignedUrlModal(file) },
                        onDelete = { viewModel.deleteFile(file.id) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(64.dp))
        }
    }

    // Create Bucket Dialog
    if (uiState.isCreateBucketDialogOpen) {
        CreateBucketDialog(
            onDismiss = { viewModel.closeCreateBucketDialog() },
            onConfirm = { name, isPublic -> viewModel.createBucket(name, isPublic) }
        )
    }

    // File Preview Modal
    uiState.selectedFileForPreview?.let { file ->
        FilePreviewDialog(
            file = file,
            onDismiss = { viewModel.selectFileForPreview(null) }
        )
    }

    // Signed URL Generator Modal
    uiState.selectedFileForSignedUrl?.let { file ->
        SignedUrlModal(
            file = file,
            signedUrl = uiState.signedUrlResult ?: "",
            onSelectExpiry = { expiry -> viewModel.generateSignedUrlWithExpiry(expiry) },
            onCopyUrl = { copyUrlToClipboard(context, uiState.signedUrlResult ?: "") },
            onDismiss = { viewModel.closeSignedUrlModal() }
        )
    }
}

@Composable
fun StorageMetricsCard(
    usedBytes: Long,
    availableBytes: Long,
    bucketCount: Int,
    fileCount: Int
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
                    text = "STORAGE DASHBOARD",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Text(
                    text = "${formatBytes(usedBytes)} / ${formatBytes(availableBytes)}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = LimeAccent
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricPill(title = "BUCKETS", value = "$bucketCount", modifier = Modifier.weight(1f))
                MetricPill(title = "TOTAL FILES", value = "$fileCount", modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun BucketSelectorRow(
    buckets: List<StorageBucket>,
    selectedBucket: StorageBucket?,
    onSelect: (StorageBucket) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        buckets.forEach { bucket ->
            val isSelected = selectedBucket?.id == bucket.id
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) LimeAccent else MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { onSelect(bucket) }
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (bucket.isPublic) Icons.Default.Public else Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = bucket.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun StorageFileItemCard(
    file: StorageFile,
    onPreview: () -> Unit,
    onGetSignedUrl: () -> Unit,
    onDelete: () -> Unit
) {
    val icon = getFileIcon(file.mimeType)

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
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(LimeAccent.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = LimeAccent)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = file.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${formatBytes(file.sizeBytes)} • ${file.mimeType}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = onPreview) {
                    Icon(imageVector = Icons.Default.Description, contentDescription = "Preview", tint = MaterialTheme.colorScheme.onSurface)
                }
                IconButton(onClick = onGetSignedUrl) {
                    Icon(imageVector = Icons.Default.Link, contentDescription = "Signed URL", tint = LimeAccent)
                }
                IconButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444))
                }
            }
        }
    }
}

@Composable
fun EmptyFilesCard(onUpload: () -> Unit) {
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
                text = "BUCKET IS EMPTY",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            Text(
                text = "No files uploaded to this storage bucket yet.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Button(
                onClick = onUpload,
                colors = ButtonDefaults.buttonColors(containerColor = LimeAccent, contentColor = Color.Black)
            ) {
                Text("Upload Sample File", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CreateBucketDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, isPublic: Boolean) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var isPublic by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Storage Bucket") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Bucket Name (e.g. avatars, receipts)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Publicly Accessible")
                    Switch(checked = isPublic, onCheckedChange = { isPublic = it })
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, isPublic) },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = LimeAccent, contentColor = Color.Black)
            ) {
                Text("Create Bucket", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun FilePreviewDialog(
    file: StorageFile,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("File Preview: ${file.name}") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(LimeAccent.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = getFileIcon(file.mimeType), contentDescription = null, modifier = Modifier.size(48.dp), tint = LimeAccent)
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("MIME Type: ${file.mimeType}", style = MaterialTheme.typography.bodyMedium)
                    Text("Size: ${formatBytes(file.sizeBytes)}", style = MaterialTheme.typography.bodyMedium)
                    Text("Path: ${file.path}", style = MaterialTheme.typography.labelMedium, color = LimeAccent)
                    Text("Created: ${formatTimestamp(file.createdTimestamp)}", style = MaterialTheme.typography.labelMedium)
                }
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
fun SignedUrlModal(
    file: StorageFile,
    signedUrl: String,
    onSelectExpiry: (SignedUrlExpiry) -> Unit,
    onCopyUrl: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Generate Signed URL") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("File: ${file.name}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                Text("Select expiration duration:")

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    OutlinedButton(onClick = { onSelectExpiry(SignedUrlExpiry.ONE_MINUTE) }, modifier = Modifier.weight(1f)) { Text("1m", fontSize = 11.sp) }
                    OutlinedButton(onClick = { onSelectExpiry(SignedUrlExpiry.TEN_MINUTES) }, modifier = Modifier.weight(1f)) { Text("10m", fontSize = 11.sp) }
                    OutlinedButton(onClick = { onSelectExpiry(SignedUrlExpiry.ONE_HOUR) }, modifier = Modifier.weight(1f)) { Text("1h", fontSize = 11.sp) }
                    OutlinedButton(onClick = { onSelectExpiry(SignedUrlExpiry.TWENTY_FOUR_HOURS) }, modifier = Modifier.weight(1f)) { Text("24h", fontSize = 11.sp) }
                }

                Text("Signed URL:", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                Text(text = signedUrl, style = MaterialTheme.typography.labelMedium, color = LimeAccent, fontWeight = FontWeight.Bold)
            }
        },
        confirmButton = {
            Button(onClick = onCopyUrl, colors = ButtonDefaults.buttonColors(containerColor = LimeAccent, contentColor = Color.Black)) {
                Text("Copy Signed URL", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

@Composable
fun MetricPill(title: String, value: String, modifier: Modifier = Modifier) {
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

private fun getFileIcon(mimeType: String): ImageVector {
    return when {
        mimeType.startsWith("image") -> Icons.Default.Image
        mimeType.startsWith("video") -> Icons.Default.VideoFile
        mimeType.startsWith("audio") -> Icons.Default.AudioFile
        mimeType.contains("pdf") -> Icons.Default.Description
        else -> Icons.Default.InsertDriveFile
    }
}

private fun copyUrlToClipboard(context: Context, url: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("NodePhone Signed URL", url)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "Signed URL copied to clipboard", Toast.LENGTH_SHORT).show()
}

private fun formatBytes(bytes: Long): String {
    if (bytes <= 0) return "0 MB"
    val mb = bytes / (1024 * 1024)
    return if (mb > 0) "$mb MB" else "${bytes / 1024} KB"
}

private fun formatTimestamp(millis: Long): String {
    if (millis <= 0) return "Just now"
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.US)
    return sdf.format(Date(millis))
}
