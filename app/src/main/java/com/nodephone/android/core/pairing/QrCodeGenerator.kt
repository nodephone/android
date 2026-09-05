package com.nodephone.android.core.pairing

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.nodephone.android.domain.pairing.model.PairingPayload
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QrCodeGenerator @Inject constructor() {

    fun generateQrBitmap(payload: PairingPayload, sizePx: Int = 512): Bitmap? {
        return try {
            val jsonPayload = """
                {
                  "deviceId": "${payload.deviceId}",
                  "deviceName": "${payload.deviceName}",
                  "pairToken": "${payload.pairToken}",
                  "pairCode": "${payload.pairCode}",
                  "serverUrl": "${payload.serverUrl}",
                  "port": ${payload.port},
                  "expiresAt": ${payload.expiresAt},
                  "serverVersion": "${payload.serverVersion}",
                  "fingerprint": "${payload.fingerprint}"
                }
            """.trimIndent()

            val hints = hashMapOf<EncodeHintType, Any>(
                EncodeHintType.MARGIN to 1,
                EncodeHintType.CHARACTER_SET to "UTF-8"
            )

            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(jsonPayload, BarcodeFormat.QR_CODE, sizePx, sizePx, hints)

            val width = bitMatrix.width
            val height = bitMatrix.height
            val pixels = IntArray(width * height)

            for (y in 0 until height) {
                val offset = y * width
                for (x in 0 until width) {
                    pixels[offset + x] = if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE
                }
            }

            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
