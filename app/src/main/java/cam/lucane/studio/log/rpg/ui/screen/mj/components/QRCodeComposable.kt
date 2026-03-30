package cam.lucane.studio.log.rpg.ui.screen.mj.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter

@Composable
fun QRCodeImage(
    content: String,
    size: Dp = 200.dp,
    modifier: Modifier = Modifier
) {
    val bitmap = remember(content) { generateQRBitmap(content) }
    bitmap?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = "QR Code de session",
            modifier = modifier.size(size)
        )
    }
}

fun generateQRBitmap(content: String, sizePx: Int = 512): Bitmap? =
    runCatching {
        val matrix = MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, sizePx, sizePx)
        Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.RGB_565).apply {
            for (x in 0 until sizePx)
                for (y in 0 until sizePx)
                    setPixel(x, y, if (matrix[x, y]) android.graphics.Color.BLACK
                    else android.graphics.Color.WHITE)
        }
    }.getOrNull()