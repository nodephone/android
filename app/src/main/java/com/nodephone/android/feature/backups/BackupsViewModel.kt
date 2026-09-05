package com.nodephone.android.feature.backups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nodephone.android.core.backup.BackupArchiveEngine
import com.nodephone.android.data.backup.BackupRepository
import com.nodephone.android.data.projects.ProjectRepository
import com.nodephone.android.domain.backup.model.BackupRecord
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class BackupsViewModel @Inject constructor(
    private val backupRepository: BackupRepository,
    private val projectRepository: ProjectRepository,
    private val archiveEngine: BackupArchiveEngine
) : ViewModel() {

    private val activeProjectId = MutableStateFlow("default")
    private val backupToRestore = MutableStateFlow<BackupRecord?>(null)
    private val backupToVerify = MutableStateFlow<BackupRecord?>(null)
    private val verificationResult = MutableStateFlow<String?>(null)
    private val isScheduleDialogOpen = MutableStateFlow(false)
    private val scheduleType = MutableStateFlow("MANUAL")
    private val retentionCount = MutableStateFlow(5)

    init {
        viewModelScope.launch {
            projectRepository.activeProject.collect { proj ->
                activeProjectId.value = proj?.id ?: "default"
            }
        }
    }

    private val backupsFlow = activeProjectId.flatMapLatest { pId ->
        backupRepository.getBackupsFlow(pId)
    }

    val uiState: StateFlow<BackupsUiState> = combine(
        backupsFlow,
        activeProjectId,
        backupToRestore,
        backupToVerify,
        verificationResult,
        isScheduleDialogOpen,
        scheduleType,
        retentionCount
    ) { args ->
        val backups = args[0] as List<BackupRecord>
        val pId = args[1] as String
        val restore = args[2] as BackupRecord?
        val verify = args[3] as BackupRecord?
        val vResult = args[4] as String?
        val scheduleOpen = args[5] as Boolean
        val sType = args[6] as String
        val retention = args[7] as Int

        val totalSize = backups.sumOf { it.sizeBytes }
        val latestDate = backups.firstOrNull()?.let {
            SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.US).format(Date(it.createdTimestamp))
        } ?: "None"

        BackupsUiState(
            backups = backups,
            activeProjectId = pId,
            totalSizeBytes = totalSize,
            latestBackupDate = latestDate,
            scheduleType = sType,
            retentionCount = retention,
            backupToRestore = restore,
            backupToVerify = verify,
            verificationResult = vResult,
            isScheduleDialogOpen = scheduleOpen
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BackupsUiState()
    )

    fun createBackupNow() {
        viewModelScope.launch {
            val pId = activeProjectId.value
            archiveEngine.createBackup(pId, scheduleType = "MANUAL")
        }
    }

    fun openRestoreDialog(record: BackupRecord) {
        backupToRestore.value = record
    }

    fun closeRestoreDialog() {
        backupToRestore.value = null
    }

    fun confirmRestore(record: BackupRecord) {
        viewModelScope.launch {
            val pId = activeProjectId.value
            archiveEngine.restoreBackup(pId, record)
            backupToRestore.value = null
        }
    }

    fun openVerifyModal(record: BackupRecord) {
        backupToVerify.value = record
        val file = java.io.File(record.filePath)
        val isValid = archiveEngine.verifyChecksum(file, record.checksum)
        verificationResult.value = if (isValid) {
            "VALID: Checksum ${record.checksum.take(12)}... matches .npbackup archive"
        } else {
            "CORRUPTED: SHA-256 hash mismatch or archive damaged!"
        }
    }

    fun closeVerifyModal() {
        backupToVerify.value = null
        verificationResult.value = null
    }

    fun deleteBackup(record: BackupRecord) {
        viewModelScope.launch {
            val file = java.io.File(record.filePath)
            if (file.exists()) file.delete()
            backupRepository.deleteBackup(record.id, record.projectId)
        }
    }

    fun openScheduleDialog() {
        isScheduleDialogOpen.value = true
    }

    fun closeScheduleDialog() {
        isScheduleDialogOpen.value = false
    }

    fun saveScheduleConfig(type: String, retention: Int) {
        scheduleType.value = type
        retentionCount.value = retention
        isScheduleDialogOpen.value = false
    }
}
