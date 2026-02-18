package cam.lucane.studio.log.rpg.ui.dialog.character

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

private const val OUTPUT_SIZE = 512

suspend fun cropImageToCircle(
    context: Context,
    sourceUri: Uri,
    scale: Float,
    offset: Offset,
    canvasSize: IntSize
): Uri? = withContext(Dispatchers.IO) {
    try {
        val sourceBitmap = loadBitmap(context, sourceUri) ?: return@withContext null
        val (fitWidth, fitHeight, fitOffsetX, fitOffsetY) = computeFitLayout(sourceBitmap, canvasSize)

        val boxSize = canvasSize.width.coerceAtMost(canvasSize.height) * 0.84f
        val canvasCenter = Offset(canvasSize.width / 2f, canvasSize.height / 2f)

        // Convertit une position canvas → coordonnées bitmap
        fun toBitmap(cx: Float, cy: Float): Pair<Float, Float> {
            val ix = (cx - offset.x - canvasCenter.x) / scale + canvasCenter.x
            val iy = (cy - offset.y - canvasCenter.y) / scale + canvasCenter.y
            return (ix - fitOffsetX) / fitWidth * sourceBitmap.width to
                    (iy - fitOffsetY) / fitHeight * sourceBitmap.height
        }

        // Coins du carré de recadrage en coordonnées bitmap
        val half = boxSize / 2f
        val (srcLeft, srcTop) = toBitmap(canvasCenter.x - half, canvasCenter.y - half)
        val (srcRight, srcBottom) = toBitmap(canvasCenter.x + half, canvasCenter.y + half)

        val output = Bitmap.createBitmap(OUTPUT_SIZE, OUTPUT_SIZE, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(output)

        // drawBitmap attend un Rect (entier) pour la source et RectF pour la destination
        // les bords arrondis sont gérés par Compose à l'affichage via clip()
        val src = android.graphics.Rect(
            srcLeft.coerceIn(0f, sourceBitmap.width.toFloat()).toInt(),
            srcTop.coerceIn(0f, sourceBitmap.height.toFloat()).toInt(),
            srcRight.coerceIn(0f, sourceBitmap.width.toFloat()).toInt(),
            srcBottom.coerceIn(0f, sourceBitmap.height.toFloat()).toInt()
        )
        val dst = android.graphics.RectF(0f, 0f, OUTPUT_SIZE.toFloat(), OUTPUT_SIZE.toFloat())
        canvas.drawBitmap(sourceBitmap, src, dst, null)

        val outputFile = File(context.cacheDir, "profile_crop_${System.currentTimeMillis()}.png")
        FileOutputStream(outputFile).use { output.compress(Bitmap.CompressFormat.PNG, 100, it) }

        Uri.fromFile(outputFile)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private fun loadBitmap(context: Context, uri: Uri): Bitmap? {
    return context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) }
}

private data class FitLayout(val width: Float, val height: Float, val offsetX: Float, val offsetY: Float)

private fun computeFitLayout(bitmap: Bitmap, canvasSize: IntSize): FitLayout {
    val bitmapAspect = bitmap.width.toFloat() / bitmap.height
    val canvasAspect = canvasSize.width.toFloat() / canvasSize.height
    val (w, h) = if (bitmapAspect > canvasAspect)
        canvasSize.width.toFloat() to canvasSize.width / bitmapAspect
    else
        canvasSize.height * bitmapAspect to canvasSize.height.toFloat()
    return FitLayout(w, h, (canvasSize.width - w) / 2f, (canvasSize.height - h) / 2f)
}