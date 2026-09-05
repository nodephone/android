package com.nodephone.android.core.pairing

import android.content.Context
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceIdentityManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("nodephone_identity", Context.MODE_PRIVATE)

    val deviceId: String by lazy {
        prefs.getString("device_id", null) ?: run {
            val newId = UUID.randomUUID().toString()
            prefs.edit().putString("device_id", newId).apply()
            newId
        }
    }

    val deviceName: String by lazy {
        val model = Build.MODEL ?: "Android Device"
        "NodePhone ($model)"
    }

    val fingerprint: String by lazy {
        generateFingerprint(deviceId)
    }

    val androidVersion: String = Build.VERSION.RELEASE ?: "Unknown"
    val nodePhoneVersion: String = "1.0.0"

    private fun generateFingerprint(id: String): String {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val hash = digest.digest(id.toByteArray(Charsets.UTF_8))
            hash.joinToString("") { "%02x".format(it) }.take(16).uppercase()
        } catch (e: Exception) {
            "NP-" + id.take(8).uppercase()
        }
    }
}
