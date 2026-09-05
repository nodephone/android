package com.nodephone.android.core.security

import com.nodephone.android.core.pairing.PairingTokenManager
import com.nodephone.android.data.pairing.PairingRepository
import com.nodephone.android.data.trusted.TrustedDeviceRepository
import com.nodephone.android.domain.pairing.model.PairingState
import com.nodephone.android.domain.trusted.model.TrustedDevice
import javax.inject.Inject
import javax.inject.Singleton

data class HandshakeResponse(
    val success: Boolean,
    val message: String,
    val sessionToken: String? = null,
    val deviceId: String? = null
)

@Singleton
class HandshakeManager @Inject constructor(
    private val tokenManager: PairingTokenManager,
    private val pairingRepository: PairingRepository,
    private val trustedDeviceRepository: TrustedDeviceRepository,
    private val sessionTokenManager: SessionTokenManager
) {
    suspend fun handlePairRequest(studioDeviceId: String, studioName: String, pairToken: String, studioIp: String): HandshakeResponse {
        val currentPayload = tokenManager.currentPayload.value
        if (currentPayload == null || currentPayload.pairToken != pairToken) {
            return HandshakeResponse(success = false, message = "Invalid or expired pairing token")
        }

        // Token valid -> Transition to PAIRING
        pairingRepository.simulatePairing(studioName = studioName, studioIp = studioIp)
        return HandshakeResponse(success = true, message = "Pairing request accepted")
    }

    suspend fun handlePairTrust(studioDeviceId: String, studioName: String, fingerprint: String, studioIp: String, studioVersion: String): HandshakeResponse {
        val sessionToken = sessionTokenManager.generateSessionToken()
        val now = System.currentTimeMillis()

        val trustedDevice = TrustedDevice(
            deviceId = studioDeviceId,
            deviceName = studioName,
            fingerprint = fingerprint,
            firstPairedTimestamp = now,
            lastConnectedTimestamp = now,
            studioVersion = studioVersion,
            connectedIp = studioIp,
            isTrusted = true,
            sessionToken = sessionToken
        )

        // Store trust record & invalidate single-use QR token
        trustedDeviceRepository.addOrUpdateTrustedDevice(trustedDevice)
        tokenManager.invalidateToken()

        return HandshakeResponse(
            success = true,
            message = "Trust relationship established",
            sessionToken = sessionToken,
            deviceId = studioDeviceId
        )
    }

    suspend fun handlePairRevoke(deviceId: String): HandshakeResponse {
        trustedDeviceRepository.revokeDevice(deviceId)
        pairingRepository.revokePairing()
        return HandshakeResponse(success = true, message = "Pairing revoked successfully")
    }

    suspend fun getPairStatus(sessionToken: String): HandshakeResponse {
        val device = sessionTokenManager.validateSession(sessionToken)
        return if (device != null) {
            HandshakeResponse(
                success = true,
                message = "Device trusted and session active",
                sessionToken = sessionToken,
                deviceId = device.deviceId
            )
        } else {
            HandshakeResponse(success = false, message = "Session invalid or revoked")
        }
    }
}
