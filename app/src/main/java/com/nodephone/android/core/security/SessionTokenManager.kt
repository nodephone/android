package com.nodephone.android.core.security

import com.nodephone.android.data.trusted.TrustedDeviceRepository
import com.nodephone.android.domain.trusted.model.TrustedDevice
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionTokenManager @Inject constructor(
    private val trustedDeviceRepository: TrustedDeviceRepository
) {
    private val secureRandom = SecureRandom()

    fun generateSessionToken(): String {
        val randomBytes = ByteArray(32)
        secureRandom.nextBytes(randomBytes)
        val tokenHex = randomBytes.joinToString("") { "%02x".format(it) }
        return "NP-SESS-$tokenHex"
    }

    suspend fun validateSession(token: String): TrustedDevice? {
        if (token.isBlank()) return null
        return trustedDeviceRepository.getDeviceBySessionToken(token)
    }
}
