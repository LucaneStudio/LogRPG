package cam.lucane.studio.log.rpg.ui.components.pdf

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.ui.theme.AccentGold
import cam.lucane.studio.log.rpg.ui.theme.AccentGreen
import cam.lucane.studio.log.rpg.ui.theme.AccentPurple
import cam.lucane.studio.log.rpg.ui.theme.AccentRed
import cam.lucane.studio.log.rpg.ui.theme.HealthRed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.use

@Composable
fun PdfViewer(
    pdfFile: File,
    characterId: Long,
    pdfLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>
) {
    var pageCount by remember { mutableStateOf(0) }
    var bitmaps by remember { mutableStateOf<List<Bitmap>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var currentPageIndex by remember { mutableStateOf(0) }

    val mainColor = remember(characterId) {
        val colors = listOf(AccentRed, AccentPurple, AccentGreen, AccentGold)
        colors[(characterId % colors.size).toInt()]
    }


    // Charger le PDF
    LaunchedEffect(pdfFile) {
        isLoading = true
        error = null
        try {
            val loadedBitmaps = withContext(Dispatchers.IO) {
                loadPdfPages(pdfFile)
            }
            bitmaps = loadedBitmaps
            pageCount = loadedBitmaps.size
            isLoading = false
        } catch (e: Exception) {
            error = "Erreur de chargement: ${e.message}"
            isLoading = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = AccentPurple
                )
            }
            error != null -> {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Error,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = HealthRed
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(error!!, color = HealthRed, fontSize = 13.sp)
                }
            }
            bitmaps.isNotEmpty() -> {
                // Zone PDF plein écran
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    PdfPageImage(
                        bitmap = bitmaps[currentPageIndex],
                        pageNumber = currentPageIndex + 1,
                        pdfLauncher = pdfLauncher,
                        mainColor = mainColor
                    )
                }

                // Overlay glassmorphique en bas
                PdfOverlay(
                    currentPage = currentPageIndex + 1,
                    totalPages = pageCount,
                    onPrevious = {
                        if (currentPageIndex > 0) currentPageIndex--
                    },
                    onNext = {
                        if (currentPageIndex < bitmaps.size - 1) currentPageIndex++
                    },
                    canGoPrevious = currentPageIndex > 0,
                    canGoNext = currentPageIndex < bitmaps.size - 1,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(12.dp),
                    mainColor = mainColor
                )
            }
        }
    }
}

// Fonction pour charger toutes les pages du PDF
private suspend fun loadPdfPages(file: File): List<Bitmap> {
    return withContext(Dispatchers.IO) {
        val bitmaps = mutableListOf<Bitmap>()

        ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY).use { descriptor ->
            PdfRenderer(descriptor).use { renderer ->
                for (pageIndex in 0 until renderer.pageCount) {
                    renderer.openPage(pageIndex).use { page ->
                        val maxWidth = 1080
                        val maxHeight = 1920

                        val aspectRatio = page.width.toFloat() / page.height.toFloat()

                        val (width, height) = if (aspectRatio > (maxWidth.toFloat() / maxHeight)) {
                            maxWidth to (maxWidth / aspectRatio).toInt()
                        } else {
                            (maxHeight * aspectRatio).toInt() to maxHeight
                        }

                        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                        bitmaps.add(bitmap)
                    }
                }
            }
        }

        bitmaps
    }
}