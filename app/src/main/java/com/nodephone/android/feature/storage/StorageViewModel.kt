package com.nodephone.android.feature.storage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nodephone.android.core.storage.SignedUrlExpiry
import com.nodephone.android.core.storage.SignedUrlGenerator
import com.nodephone.android.data.projects.ProjectRepository
import com.nodephone.android.data.storage.StorageRepository
import com.nodephone.android.domain.storage.model.StorageBucket
import com.nodephone.android.domain.storage.model.StorageFile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class StorageViewModel @Inject constructor(
    private val storageRepository: StorageRepository,
    private val projectRepository: ProjectRepository,
    private val signedUrlGenerator: SignedUrlGenerator
) : ViewModel() {

    private val activeProjectId = MutableStateFlow("default")
    private val selectedBucketId = MutableStateFlow<String?>(null)
    private val searchQuery = MutableStateFlow("")
    private val isGridView = MutableStateFlow(false)
    private val selectedFileForPreview = MutableStateFlow<StorageFile?>(null)
    private val selectedFileForSignedUrl = MutableStateFlow<StorageFile?>(null)
    private val signedUrlResult = MutableStateFlow<String?>(null)
    private val isCreateBucketDialogOpen = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            projectRepository.activeProject.collect { proj ->
                val pId = proj?.id ?: "default"
                activeProjectId.value = pId
                // Ensure default bucket exists
                val buckets = storageRepository.getBucketsFlow(pId).value
                if (buckets.isEmpty()) {
                    storageRepository.createBucket(pId, "public", isPublic = true)
                    storageRepository.createBucket(pId, "private", isPublic = false)
                }
            }
        }
    }

    private val bucketsFlow = activeProjectId.flatMapLatest { pId ->
        storageRepository.getBucketsFlow(pId)
    }

    private val filesFlow = combine(activeProjectId, selectedBucketId, bucketsFlow) { pId, sBId, buckets ->
        val targetBucket = sBId ?: buckets.firstOrNull()?.id ?: "public"
        Pair(pId, targetBucket)
    }.flatMapLatest { (pId, bucketId) ->
        storageRepository.getFilesFlow(bucketId, pId)
    }

    val uiState: StateFlow<StorageUiState> = combine(
        bucketsFlow,
        filesFlow,
        selectedBucketId,
        searchQuery,
        isGridView,
        selectedFileForPreview,
        selectedFileForSignedUrl,
        signedUrlResult,
        isCreateBucketDialogOpen
    ) { args ->
        val buckets = args[0] as List<StorageBucket>
        val files = args[1] as List<StorageFile>
        val sBId = args[2] as String?
        val query = args[3] as String
        val grid = args[4] as Boolean
        val preview = args[5] as StorageFile?
        val signTarget = args[6] as StorageFile?
        val signResult = args[7] as String?
        val createOpen = args[8] as Boolean

        val currentBucket = buckets.find { it.id == sBId } ?: buckets.firstOrNull()
        val filteredFiles = if (query.isBlank()) files else files.filter { it.name.contains(query, ignoreCase = true) }
        val totalBytes = files.sumOf { it.sizeBytes }

        StorageUiState(
            buckets = buckets,
            selectedBucket = currentBucket,
            files = filteredFiles,
            totalUsedBytes = totalBytes,
            searchQuery = query,
            isGridView = grid,
            selectedFileForPreview = preview,
            selectedFileForSignedUrl = signTarget,
            signedUrlResult = signResult,
            isCreateBucketDialogOpen = createOpen
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StorageUiState()
    )

    fun selectBucket(bucket: StorageBucket) {
        selectedBucketId.value = bucket.id
    }

    fun toggleViewMode() {
        isGridView.value = !isGridView.value
    }

    fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
    }

    fun openCreateBucketDialog() {
        isCreateBucketDialogOpen.value = true
    }

    fun closeCreateBucketDialog() {
        isCreateBucketDialogOpen.value = false
    }

    fun createBucket(name: String, isPublic: Boolean) {
        viewModelScope.launch {
            if (name.isNotBlank()) {
                val pId = activeProjectId.value
                val newBucket = storageRepository.createBucket(pId, name.trim(), isPublic)
                selectedBucketId.value = newBucket.id
            }
            isCreateBucketDialogOpen.value = false
        }
    }

    fun deleteBucket(bucketId: String) {
        viewModelScope.launch {
            val pId = activeProjectId.value
            storageRepository.deleteBucket(bucketId, pId)
            selectedBucketId.value = null
        }
    }

    fun uploadSampleFile(fileName: String, mimeType: String) {
        viewModelScope.launch {
            val pId = activeProjectId.value
            val bId = selectedBucketId.value ?: "public"
            val file = StorageFile(
                id = UUID.randomUUID().toString(),
                bucketId = bId,
                projectId = pId,
                name = fileName,
                path = "/NodePhone/projects/$pId/storage/$bId/$fileName",
                mimeType = mimeType,
                sizeBytes = (512 * 1024L..1024 * 1024 * 4L).random(),
                createdTimestamp = System.currentTimeMillis()
            )
            storageRepository.addFile(file)
        }
    }

    fun deleteFile(fileId: String) {
        viewModelScope.launch {
            val pId = activeProjectId.value
            storageRepository.deleteFile(fileId, pId)
        }
    }

    fun openSignedUrlModal(file: StorageFile) {
        selectedFileForSignedUrl.value = file
        signedUrlResult.value = signedUrlGenerator.generateSignedUrl(file.bucketId, file.name, SignedUrlExpiry.ONE_HOUR)
    }

    fun generateSignedUrlWithExpiry(expiry: SignedUrlExpiry) {
        val file = selectedFileForSignedUrl.value ?: return
        signedUrlResult.value = signedUrlGenerator.generateSignedUrl(file.bucketId, file.name, expiry)
    }

    fun closeSignedUrlModal() {
        selectedFileForSignedUrl.value = null
        signedUrlResult.value = null
    }

    fun selectFileForPreview(file: StorageFile?) {
        selectedFileForPreview.value = file
    }
}
