package com.nodephone.android.core.server

import com.nodephone.android.core.security.SessionTokenManager
import com.nodephone.android.core.storage.SignedUrlExpiry
import com.nodephone.android.core.storage.SignedUrlGenerator
import com.nodephone.android.data.storage.StorageRepository
import com.nodephone.android.domain.storage.model.StorageBucket
import com.nodephone.android.domain.storage.model.StorageFile
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudioStorageHandler @Inject constructor(
    private val sessionTokenManager: SessionTokenManager,
    private val storageRepository: StorageRepository,
    private val signedUrlGenerator: SignedUrlGenerator
) {
    suspend fun listBuckets(sessionToken: String, projectId: String): ApiResponse<List<StorageBucket>> {
        sessionTokenManager.validateSession(sessionToken)
            ?: return ApiResponse(success = false, error = "Unauthorized")

        val buckets = storageRepository.getBucketsFlow(projectId).value
        return ApiResponse(success = true, data = buckets)
    }

    suspend fun createBucket(sessionToken: String, projectId: String, name: String, isPublic: Boolean): ApiResponse<StorageBucket> {
        sessionTokenManager.validateSession(sessionToken)
            ?: return ApiResponse(success = false, error = "Unauthorized")

        val bucket = storageRepository.createBucket(projectId, name, isPublic)
        return ApiResponse(success = true, data = bucket)
    }

    suspend fun listObjects(sessionToken: String, projectId: String, bucketId: String): ApiResponse<List<StorageFile>> {
        sessionTokenManager.validateSession(sessionToken)
            ?: return ApiResponse(success = false, error = "Unauthorized")

        val files = storageRepository.getFilesFlow(bucketId, projectId).value
        return ApiResponse(success = true, data = files)
    }

    suspend fun createSignedUrl(sessionToken: String, bucketId: String, fileName: String, expiryLabel: String): ApiResponse<String> {
        sessionTokenManager.validateSession(sessionToken)
            ?: return ApiResponse(success = false, error = "Unauthorized")

        val expiry = when (expiryLabel.lowercase()) {
            "1m", "1 minute" -> SignedUrlExpiry.ONE_MINUTE
            "10m", "10 minutes" -> SignedUrlExpiry.TEN_MINUTES
            "24h", "24 hours" -> SignedUrlExpiry.TWENTY_FOUR_HOURS
            else -> SignedUrlExpiry.ONE_HOUR
        }

        val url = signedUrlGenerator.generateSignedUrl(bucketId, fileName, expiry)
        return ApiResponse(success = true, data = url)
    }
}
