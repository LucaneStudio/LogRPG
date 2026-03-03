package cam.lucane.studio.log.rpg.ui.utils

import android.graphics.Bitmap
import android.util.Base64
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

object QrCodeUtils {

    // JSON → compressé → Base64 → QR Bitmap
    fun generateQrBitmap(json: String, size: Int = 600): Bitmap? {
        return try {
            val compressed = compress(json)
            val encoded = Base64.encodeToString(compressed, Base64.NO_WRAP)
            if (encoded.length > 2953) return null
            val hints = mapOf(
                EncodeHintType.MARGIN to 1,
                EncodeHintType.ERROR_CORRECTION to com.google.zxing.qrcode.decoder.ErrorCorrectionLevel.L  // ← L au lieu de M
            )
            val matrix = MultiFormatWriter().encode(
                encoded, BarcodeFormat.QR_CODE, size, size, hints
            )
            BarcodeEncoder().createBitmap(matrix)
        } catch (e: Exception) { null }
    }

    // QR scan result → JSON
    fun decodeQrPayload(raw: String): String? {
        return try {
            val bytes = Base64.decode(raw, Base64.NO_WRAP)
            decompress(bytes)
        } catch (e: Exception) { null }
    }

    // Taille estimée (pour afficher "✓ QR possible" ou "⚠ Trop lourd")
    fun estimateQrSize(json: String): Int {
        val compressed = compress(json)
        return Base64.encodeToString(compressed, Base64.NO_WRAP).length
    }

    private fun compress(input: String): ByteArray {
        val out = ByteArrayOutputStream()
        GZIPOutputStream(out).use { it.write(input.toByteArray(Charsets.UTF_8)) }
        return out.toByteArray()
    }

    private fun decompress(bytes: ByteArray): String {
        return GZIPInputStream(ByteArrayInputStream(bytes))
            .bufferedReader(Charsets.UTF_8).readText()
    }
}