package cam.lucane.studio.log.rpg.ui.screen.tabs

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.ui.theme.*
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun PdfViewer(
    pdfFile: File,
    pdfLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>
) {
    var pageCount by remember { mutableStateOf(0) }
    var bitmaps by remember { mutableStateOf<List<Bitmap>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var currentPageIndex by remember { mutableStateOf(0) }

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
                        pdfLauncher = pdfLauncher
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
                        .padding(12.dp)
                )
            }
        }
    }
}

@Composable
private fun PdfPageImage(
    bitmap: Bitmap,
    pageNumber: Int,
    pdfLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>
) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val buttonColor = BackgroundDark.copy(alpha = 0.85f)
    val buttonShape = RoundedCornerShape(10.dp)
    val buttonBorder = BorderStroke(1.dp, BorderSubtle)

    Box(
        modifier = Modifier.fillMaxSize().clip(RectangleShape),
        contentAlignment = Alignment.Center
    ) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Page $pageNumber",
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = { tapOffset ->
                            if (scale > 1f) {
                                scale = 1f
                                offset = Offset.Zero
                            } else {
                                scale = 1.7f
                                val centerX = size.width / 2f
                                val centerY = size.height / 2f
                                val offsetX = (centerX - tapOffset.x) * (scale - 1f)
                                val offsetY = (centerY - tapOffset.y) * (scale - 1f)
                                offset = Offset(offsetX, offsetY)
                            }
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        val newScale = (scale * zoom).coerceIn(1f, 5f)

                        if (newScale > 1f) {
                            scale = newScale
                            val maxX = (size.width * (scale - 1)) / 2
                            val maxY = (size.height * (scale - 1)) / 2
                            offset = Offset(
                                x = (offset.x + pan.x).coerceIn(-maxX, maxX),
                                y = (offset.y + pan.y).coerceIn(-maxY, maxY)
                            )
                        } else {
                            scale = 1f
                            offset = Offset.Zero
                        }
                    }
                }
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                ),
            contentScale = ContentScale.Fit
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {

                // Badge numéro de page
                Box(
                    modifier = Modifier
                        .background(color = buttonColor, shape = buttonShape)
                        .border(border = buttonBorder, shape = buttonShape),
                ) {
                    Text(
                        text = "$pageNumber",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        fontSize = 11.sp,
                        color = AccentPurple,
                        fontWeight = FontWeight.Bold
                    )
                }
                // Badge de zoom (si zoomé)
                if (scale > 1f) {
                    Box(
                        modifier = Modifier
                            .background(color = buttonColor, shape = buttonShape)
                            .border(border = buttonBorder, shape = buttonShape),
                    ) {
                        Text(
                            text = "${(scale * 100).toInt()}%",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            fontSize = 11.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .background(color = buttonColor, shape = buttonShape)
                    .border(border = buttonBorder, shape = buttonShape)
                    .clickable {
                        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                            addCategory(Intent.CATEGORY_OPENABLE)
                            type = "application/pdf"
                        }
                        pdfLauncher.launch(intent)
                    }
                    .clip(buttonShape)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Edit,
                        "Modifier",
                        modifier = Modifier.size(14.dp),
                        tint = AccentPurple
                    )
                    Text(
                        text = "Modifier",
                        fontSize = 11.sp,
                        color = AccentPurple,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

    }
}

@Composable
private fun PdfOverlay(
    currentPage: Int,
    totalPages: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    canGoPrevious: Boolean,
    canGoNext: Boolean,
    modifier: Modifier = Modifier
) {
    val hazeState = remember { HazeState() }

    Surface(
        modifier = modifier.haze(hazeState).fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = BackgroundDark.copy(alpha = 0.85f),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle.copy(alpha = 0.5f)),
        shadowElevation = 8.dp
    ) {
        // Effet glassmorphic en arrière-plan
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.03f),
                            Color.Transparent
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Ligne navigation de page
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onPrevious,
                        enabled = canGoPrevious,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (canGoPrevious) TextPrimary else TextSecondary.copy(alpha = 0.3f)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (canGoPrevious) BorderSubtle else BorderSubtle.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            "Précédent",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Précédent", fontSize = 11.sp)
                    }

                    Text(
                        text = "Page $currentPage / $totalPages",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )

                    OutlinedButton(
                        onClick = onNext,
                        enabled = canGoNext,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (canGoNext) TextPrimary else TextSecondary.copy(alpha = 0.3f)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (canGoNext) BorderSubtle else BorderSubtle.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Suivant", fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            Icons.Default.ArrowForward,
                            "Suivant",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
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