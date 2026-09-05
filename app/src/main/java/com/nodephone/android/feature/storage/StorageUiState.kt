package com.nodephone.android.feature.storage

import com.nodephone.android.core.storage.SignedUrlExpiry
import com.nodephone.android.domain.storage.model.StorageBucket
import com.nodephone.android.domain.storage.model.StorageFile

data class StorageUiState(
    val buckets: List<StorageBucket> = emptyList(),
    val selectedBucket: StorageBucket? = null,
    val files: List<StorageFile> = emptyList(),
    val totalUsedBytes: Long = 0L,
    val availableDeviceBytes: Long = 1024L * 1024L * 1024L * 64L, // 64 GB
    val searchQuery: String = "",
    val isGridView: Boolean = false,
    val selectedFileForPreview: StorageFile? = null,
    val selectedFileForSignedUrl: StorageFile? = null,
    val signedUrlResult: String? = null,
    val isCreateBucketDialogOpen: Boolean = false
)
