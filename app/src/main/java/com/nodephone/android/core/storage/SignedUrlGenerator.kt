package com.nodephone.android.core.storage

import com.nodephone.android.core.network.NetworkUtils
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

enum class SignedUrlExpiry(val seconds: Long, val label: String) {
    ONE_MINUTE(60L, "1 Minute"),
    TEN_MINUTES(600L, "10 Minutes"),
    ONE_HOUR(3600L, "1 Hour"),
    TWENTY_FOUR_HOURS(86400L, "24 Hours")
}

@Singleton
class SignedUrlGenerator @Inject constructor(
    private val networkUtils: NetworkUtils
) {
    fun generateSignedUrl(bucketId: String, fileName: String, expiry: SignedUrlExpiry): String {
        val baseUrl = networkUtils.getServerUrl(port = 8080)
        val expiresAt = System.currentTimeMillis() + (expiry.seconds * 1000L)
        val rawSecret = "$bucketId:$fileName:$expiresAt:nodephone_secret_key"

        val token = try {
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(rawSecret.toByteArray(Charsets.UTF_8))
            digest.joinToString("") { "%02x".format(it) }.take(16)
        } catch (e: Exception) {
            "sign_" + System.currentTimeMillis()
        }

        return "$baseUrl/storage/v1/object/sign/$bucketId/$fileName?token=$token&expires=$expiresAt"
    }
}
