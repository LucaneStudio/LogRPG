package cam.lucane.studio.log.rpg.ui.screen.qr

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.theme.NunitoFontFamily
import cam.lucane.studio.log.rpg.utils.MultiQrCodeUtils
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import java.util.concurrent.Executors

// État de chaque QR attendu
enum class QrScanState { PENDING, SCANNING, DONE, SKIPPED }

data class QrScanItem(
    val type: MultiQrCodeUtils.QrType,
    val label: String,
    val emoji: String,
    val required: Boolean,
    val state: QrScanState = if (type == MultiQrCodeUtils.QrType.STATS) QrScanState.SCANNING else QrScanState.PENDING,
    val raw: String? = null
)

@Composable
fun MultiQrScannerScreen(
    onComplete: (stats: String, abilities: String?, items: String?, notes: String?) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasPermission by remember { mutableStateOf(false) }
    var lastScanned by remember { mutableStateOf("") }
    // ✅ Flash vert au scan réussi
    var flashVisible by remember { mutableStateOf(false) }
    var flashMessage by remember { mutableStateOf("") }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { hasPermission = it }

    LaunchedEffect(Unit) {
        val perm = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        hasPermission = perm == android.content.pm.PackageManager.PERMISSION_GRANTED
        if (!hasPermission) permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    // État des 4 QR
    var items by remember {
        mutableStateOf(listOf(
            QrScanItem(MultiQrCodeUtils.QrType.STATS,      "Stats",      "🎲", required = true,  state = QrScanState.SCANNING),
            QrScanItem(MultiQrCodeUtils.QrType.ABILITIES,  "Sorts",      "📖", required = false),
            QrScanItem(MultiQrCodeUtils.QrType.ITEMS,      "Inventaire", "🎒", required = false),
            QrScanItem(MultiQrCodeUtils.QrType.NOTES,      "Notes",      "📝", required = false)
        ))
    }

    // QR Stats scanné = le bouton Terminer devient disponible
    val statsScanned = items.any { it.type == MultiQrCodeUtils.QrType.STATS && it.state == QrScanState.DONE }

    // Index de l'item en cours de scan
    val currentScanningIndex = items.indexOfFirst { it.state == QrScanState.SCANNING }
    val currentItem = items.getOrNull(currentScanningIndex)

    // Avancer vers le prochain PENDING ou terminer
    fun advanceToNext(fromIndex: Int, newItems: List<QrScanItem>) {
        val nextPending = newItems.indexOfFirst { it.state == QrScanState.PENDING }
        if (nextPending == -1) {
            // Tout traité → finaliser
            val stats = newItems.find { it.type == MultiQrCodeUtils.QrType.STATS }?.raw ?: return
            onComplete(
                stats,
                newItems.find { it.type == MultiQrCodeUtils.QrType.ABILITIES && it.state == QrScanState.DONE }?.raw,
                newItems.find { it.type == MultiQrCodeUtils.QrType.ITEMS && it.state == QrScanState.DONE }?.raw,
                newItems.find { it.type == MultiQrCodeUtils.QrType.NOTES && it.state == QrScanState.DONE }?.raw
            )
        } else {
            items = newItems.mapIndexed { i, item ->
                if (i == nextPending) item.copy(state = QrScanState.SCANNING) else item
            }
        }
    }

    fun onSkip() {
        if (currentScanningIndex == -1) return
        val updated = items.mapIndexed { i, item ->
            if (i == currentScanningIndex) item.copy(state = QrScanState.SKIPPED) else item
        }
        advanceToNext(currentScanningIndex, updated)
    }

    fun onTerminate() {
        if (!statsScanned) {
            Toast.makeText(context, "⚠️ Le QR Stats est obligatoire", Toast.LENGTH_SHORT).show()
            return
        }
        val stats = items.find { it.type == MultiQrCodeUtils.QrType.STATS }?.raw ?: return
        onComplete(
            stats,
            items.find { it.type == MultiQrCodeUtils.QrType.ABILITIES && it.state == QrScanState.DONE }?.raw,
            items.find { it.type == MultiQrCodeUtils.QrType.ITEMS && it.state == QrScanState.DONE }?.raw,
            items.find { it.type == MultiQrCodeUtils.QrType.NOTES && it.state == QrScanState.DONE }?.raw
        )
    }

    fun onQrScanned(raw: String) {
        val type = MultiQrCodeUtils.detectType(raw) ?: return
        // Ignorer si ce type est déjà fait
        if (items.any { it.type == type && it.state == QrScanState.DONE }) return
        // Marquer comme DONE
        val updated = items.map { item ->
            when {
                item.type == type -> item.copy(state = QrScanState.DONE, raw = raw)
                item.state == QrScanState.SCANNING && item.type != type -> item.copy(state = QrScanState.PENDING)
                else -> item
            }
        }
        // ✅ Flash de confirmation
        val scannedItem = updated.find { it.type == type }
        flashMessage = "✓ ${scannedItem?.label ?: "QR"} scanné !"
        flashVisible = true
        // Avancer au suivant PENDING après le type scanné
        val doneIndex = updated.indexOfFirst { it.type == type }
        advanceToNext(doneIndex, updated)
    }

    // ── UI ────────────────────────────────────────────
    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFF0D1014))
    ) {
        // Status bar
        Box(modifier = Modifier.fillMaxWidth().height(42.dp).padding(horizontal = 22.dp, vertical = 10.dp)) {
            Text("9:41", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold,
                color = Color.White.copy(.6f))
            Text("●●● 📶 🔋", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold,
                color = Color.White.copy(.6f), modifier = Modifier.align(Alignment.CenterEnd))
        }

        // Topbar
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier.size(36.dp).clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(.1f))
                    .border(1.5.dp, Color.White.copy(.15f), RoundedCornerShape(12.dp))
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) { Text("←", fontSize = 18.sp, color = Color.White.copy(.8f)) }

            Column(Modifier.weight(1f)) {
                val doneCount = items.count { it.state == QrScanState.DONE }
                Text("Scanner les QR", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold,
                    fontFamily = NunitoFontFamily, color = Color.White)
                Text("$doneCount / 4 · Stats ★ obligatoire", fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold, fontFamily = NunitoFontFamily,
                    color = Color.White.copy(.4f))
            }

            // Bouton Terminer (dispo dès que Stats est ok)
            if (statsScanned) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(99.dp))
                        .background(ColorsSystem.GreenLight.copy(.2f))
                        .border(1.5.dp, ColorsSystem.Green.copy(.4f), RoundedCornerShape(99.dp))
                        .clickable { onTerminate() }
                        .padding(horizontal = 12.dp, vertical = 5.dp)
                ) {
                    Text("Terminer", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold,
                        fontFamily = NunitoFontFamily, color = ColorsSystem.Green)
                }
            }
        }

        // Barre de progression des 4 QR
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items.forEach { item ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(when (item.state) {
                            QrScanState.DONE -> ColorsSystem.GreenLight.copy(.2f)
                            QrScanState.SCANNING -> Color.White.copy(.12f)
                            QrScanState.SKIPPED -> Color.Red.copy(.1f)
                            QrScanState.PENDING -> Color.White.copy(.05f)
                        })
                        .padding(vertical = 6.dp, horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(item.emoji, fontSize = 14.sp)
                        Text(
                            "${item.label}${if (item.required) " ★" else ""}",
                            fontSize = 7.5.sp, fontWeight = FontWeight.ExtraBold,
                            fontFamily = NunitoFontFamily,
                            letterSpacing = .3.sp,
                            color = when (item.state) {
                                QrScanState.DONE -> ColorsSystem.Green
                                QrScanState.SCANNING -> Color.White.copy(.8f)
                                QrScanState.SKIPPED -> Color.Red.copy(.6f)
                                QrScanState.PENDING -> Color.White.copy(.3f)
                            }
                        )
                        Box(
                            modifier = Modifier.size(18.dp).clip(RoundedCornerShape(99.dp))
                                .background(when (item.state) {
                                    QrScanState.DONE -> ColorsSystem.GreenDark
                                    QrScanState.SCANNING -> Color.White.copy(.15f)
                                    QrScanState.SKIPPED -> Color.Red.copy(.3f)
                                    QrScanState.PENDING -> Color.White.copy(.05f)
                                }),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(when (item.state) {
                                QrScanState.DONE -> "✓"
                                QrScanState.SCANNING -> "…"
                                QrScanState.SKIPPED -> "—"
                                QrScanState.PENDING -> "·"
                            }, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold,
                                color = when (item.state) {
                                    QrScanState.DONE -> Color.White
                                    QrScanState.SCANNING -> Color.White.copy(.6f)
                                    QrScanState.SKIPPED -> Color.Red.copy(.7f)
                                    QrScanState.PENDING -> Color.White.copy(.25f)
                                })
                        }
                    }
                }
            }
        }

        // Caméra + overlay
        Box(modifier = Modifier.weight(1f)) {
            if (hasPermission) {
                AndroidView(
                    factory = { ctx ->
                        val previewView = PreviewView(ctx)
                        val future = ProcessCameraProvider.getInstance(ctx)
                        val executor = Executors.newSingleThreadExecutor()
                        future.addListener({
                            val provider = future.get()
                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }
                            val analysis = ImageAnalysis.Builder()
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build()
                            analysis.setAnalyzer(executor) { imageProxy ->
                                val buffer = imageProxy.planes[0].buffer
                                val bytes = ByteArray(buffer.remaining()).also { buffer.get(it) }
                                val source = PlanarYUVLuminanceSource(
                                    bytes, imageProxy.width, imageProxy.height,
                                    0, 0, imageProxy.width, imageProxy.height, false
                                )
                                try {
                                    val result = MultiFormatReader().decode(BinaryBitmap(HybridBinarizer(source)))
                                    val raw = result.text
                                    if (raw != lastScanned) {
                                        lastScanned = raw
                                        ContextCompat.getMainExecutor(ctx).execute {
                                            onQrScanned(raw)
                                        }
                                    }
                                } catch (_: NotFoundException) {}
                                imageProxy.close()
                            }
                            provider.unbindAll()
                            provider.bindToLifecycle(lifecycleOwner,
                                CameraSelector.DEFAULT_BACK_CAMERA, preview, analysis)
                        }, ContextCompat.getMainExecutor(ctx))
                        previewView
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Overlay scan
            ScanOverlay()

            // ✅ Flash vert de confirmation au scan
            LaunchedEffect(flashVisible) {
                if (flashVisible) {
                    kotlinx.coroutines.delay(1200)
                    flashVisible = false
                }
            }
            // ✅ Fix: modifier n'existe pas sur AnimatedVisibility standalone
            // Utilisation du qualificatif complet pour éviter ColumnScope.AnimatedVisibility
            androidx.compose.animation.AnimatedVisibility(
                visible = flashVisible,
                enter = fadeIn(animationSpec = tween(100)),
                exit  = fadeOut(animationSpec = tween(600))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(3.dp, ColorsSystem.Green.copy(.85f)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(ColorsSystem.GreenDark.copy(.93f))
                            .border(1.5.dp, ColorsSystem.Green, RoundedCornerShape(16.dp))
                            .padding(horizontal = 32.dp, vertical = 18.dp)
                    ) {
                        Text(
                            flashMessage,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = NunitoFontFamily,
                            color = Color.White
                        )
                    }
                }
            }

            // Hint QR attendu
            if (currentItem != null) {
                Box(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp)
                        .clip(RoundedCornerShape(99.dp))
                        .background(Color.Black.copy(.6f))
                        .border(1.dp, Color.White.copy(.15f), RoundedCornerShape(99.dp))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text("${currentItem.emoji} En attente du QR ${currentItem.label}",
                        fontSize = 11.sp, fontWeight = FontWeight.ExtraBold,
                        fontFamily = NunitoFontFamily, color = Color.White)
                }
            }
        }

        // Bottom : Passer + Terminer
        Column(
            modifier = Modifier.fillMaxWidth()
                .background(Color.Black.copy(.45f))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Passer — disponible si le QR courant n'est pas obligatoire
                val canSkip = currentItem != null && !currentItem.required
                Box(
                    modifier = Modifier.weight(1f).clip(RoundedCornerShape(99.dp))
                        .background(Color.White.copy(if (canSkip) .08f else .03f))
                        .border(1.5.dp, Color.White.copy(if (canSkip) .15f else .05f), RoundedCornerShape(99.dp))
                        .clickable(enabled = canSkip) { onSkip() }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("⏭ Passer ce QR", fontSize = 11.5.sp, fontWeight = FontWeight.ExtraBold,
                        fontFamily = NunitoFontFamily,
                        color = Color.White.copy(if (canSkip) .6f else .2f))
                }

                // Terminer — disponible dès que Stats est scanné
                Box(
                    modifier = Modifier.weight(1f).clip(RoundedCornerShape(99.dp))
                        .background(if (statsScanned) ColorsSystem.GreenLight.copy(.15f) else Color.White.copy(.03f))
                        .border(1.5.dp,
                            if (statsScanned) ColorsSystem.Green.copy(.3f) else Color.White.copy(.05f),
                            RoundedCornerShape(99.dp))
                        .clickable(enabled = statsScanned) { onTerminate() }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("✓ Terminer", fontSize = 11.5.sp, fontWeight = FontWeight.ExtraBold,
                        fontFamily = NunitoFontFamily,
                        color = if (statsScanned) ColorsSystem.Green else Color.White.copy(.2f))
                }
            }

            val skippedCount = items.count { it.state == QrScanState.SKIPPED }
            Text(
                if (skippedCount > 0)
                    "Seul le QR Stats ★ est obligatoire · $skippedCount passé${if (skippedCount > 1) "s" else ""}"
                else
                    "Seul le QR Stats ★ est obligatoire",
                modifier = Modifier.fillMaxWidth(),
                fontSize = 10.sp, fontWeight = FontWeight.SemiBold,
                fontFamily = NunitoFontFamily, color = Color.White.copy(.3f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
private fun ScanOverlay() {
    val green = ColorsSystem.Green
    val infiniteTransition = rememberInfiniteTransition(label = "scan")
    val scanY by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "scanY"
    )

    Box(modifier = Modifier.fillMaxSize().drawWithContent {
        drawContent()
        val framePx = 210.dp.toPx()
        val cx = size.width / 2f; val cy = size.height / 2f
        val left = cx - framePx / 2f; val top = cy - framePx / 2f

        drawRect(Color.Black.copy(.55f), size = androidx.compose.ui.geometry.Size(size.width, top))
        drawRect(Color.Black.copy(.55f), topLeft = Offset(0f, top + framePx),
            size = androidx.compose.ui.geometry.Size(size.width, size.height - top - framePx))
        drawRect(Color.Black.copy(.55f), topLeft = Offset(0f, top),
            size = androidx.compose.ui.geometry.Size(left, framePx))
        drawRect(Color.Black.copy(.55f), topLeft = Offset(left + framePx, top),
            size = androidx.compose.ui.geometry.Size(size.width - left - framePx, framePx))

        val cLen = 24.dp.toPx(); val sw = 3.dp.toPx()
        listOf(
            Offset(left, top) to Pair(1f, 1f), Offset(left + framePx, top) to Pair(-1f, 1f),
            Offset(left, top + framePx) to Pair(1f, -1f), Offset(left + framePx, top + framePx) to Pair(-1f, -1f)
        ).forEach { (o, d) ->
            drawLine(green, o, Offset(o.x + d.first * cLen, o.y), sw)
            drawLine(green, o, Offset(o.x, o.y + d.second * cLen), sw)
        }

        val lineY = top + scanY * (framePx - 4.dp.toPx()) + 2.dp.toPx()
        drawLine(
            brush = Brush.horizontalGradient(listOf(Color.Transparent, green, Color.Transparent),
                startX = left, endX = left + framePx),
            start = Offset(left, lineY), end = Offset(left + framePx, lineY),
            strokeWidth = 2.dp.toPx()
        )
    })
}