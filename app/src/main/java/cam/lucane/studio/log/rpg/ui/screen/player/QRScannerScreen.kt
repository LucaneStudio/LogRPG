package cam.lucane.studio.log.rpg.ui.screen.player

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import cam.lucane.studio.log.rpg.ui.theme.*
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

@Composable
fun QRScannerScreen(
    onQRScanned         : (String) -> Unit,
    onEnterCodeManually : () -> Unit,
    onCancel            : () -> Unit,
) {
    val context = LocalContext.current
    var hasCameraPermission by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasCameraPermission = granted }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorsSystem.BackgroundApp)
            .padding(horizontal = 16.dp)
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Spacer(Modifier.height(8.dp))

        Text(
            text          = "REJOINDRE UNE SESSION",
            fontSize      = 10.sp,
            fontWeight    = FontWeight.ExtraBold,
            color         = ColorsSystem.TextDisabled,
            letterSpacing = 1.5.sp,
            fontFamily    = NunitoFontFamily,
            modifier      = Modifier.align(Alignment.Start),
        )

        Text(
            text       = "Scannez le QR Code affiché\npar votre Maître de Jeu",
            fontSize   = 13.sp,
            fontWeight = FontWeight.Bold,
            color      = ColorsSystem.TextSecondary,
            fontFamily = NunitoFontFamily,
            textAlign  = TextAlign.Center,
        )

        // ── Zone caméra ────────────────────────────────────────────────────────
        if (hasCameraPermission) {
            CameraPreview(
                modifier = Modifier
                    .size(260.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(2.dp, ColorsSystem.Green, RoundedCornerShape(16.dp)),
                onQRDetected = onQRScanned,
            )
        } else {
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(ColorsSystem.BackgroundCard),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text("📷", fontSize = 32.sp)
                    Text(
                        "Permission caméra requise",
                        fontSize   = 12.sp,
                        color      = ColorsSystem.TextSecondary,
                        fontFamily = NunitoFontFamily,
                    )
                    TextButton(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                        Text(
                            "Autoriser",
                            color      = ColorsSystem.Green,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = NunitoFontFamily,
                        )
                    }
                }
            }
        }

        Text(
            text      = "Pointez la caméra vers le QR Code",
            fontSize  = 11.sp,
            color     = ColorsSystem.TextDisabled,
            fontFamily = NunitoFontFamily,
        )

        // ── Séparateur ─────────────────────────────────────────────────────────
        Row(
            modifier          = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = ColorsSystem.Divider)
            Text("ou", fontSize = 11.sp, color = ColorsSystem.TextDisabled, fontFamily = NunitoFontFamily)
            HorizontalDivider(modifier = Modifier.weight(1f), color = ColorsSystem.Divider)
        }

        Spacer(Modifier.weight(1f))

        // ── Bouton saisie manuelle ─────────────────────────────────────────────
        Button(
            onClick  = onEnterCodeManually,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape    = RoundedCornerShape(14.dp),
            colors   = ButtonDefaults.buttonColors(containerColor = AccentPurple),
        ) {
            Text(
                text       = "🔑  Entrer un code à la place",
                fontWeight = FontWeight.ExtraBold,
                fontFamily = NunitoFontFamily,
                color      = ColorsSystem.BackgroundCard,
            )
        }

        // ── Bouton annuler ─────────────────────────────────────────────────────
        TextButton(
            onClick  = onCancel,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text      = "Annuler",
                color     = ColorsSystem.TextSecondary,
                fontFamily = NunitoFontFamily,
            )
        }

        Spacer(Modifier.height(8.dp))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Caméra MLKit (inchangée)
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalGetImage::class)
@Composable
private fun CameraPreview(
    modifier     : Modifier,
    onQRDetected : (String) -> Unit,
) {
    val context        = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var scanned        by remember { mutableStateOf(false) }

    AndroidView(
        modifier = modifier,
        factory  = { ctx ->
            val previewView = PreviewView(ctx)
            val executor    = Executors.newSingleThreadExecutor()
            val future      = ProcessCameraProvider.getInstance(ctx)

            future.addListener({
                val provider = future.get()
                val preview  = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                val scanner  = BarcodeScanning.getClient()
                val analysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { ia ->
                        ia.setAnalyzer(executor) { proxy ->
                            if (scanned) { proxy.close(); return@setAnalyzer }
                            val mediaImage = proxy.image
                            if (mediaImage != null) {
                                val image = InputImage.fromMediaImage(mediaImage, proxy.imageInfo.rotationDegrees)
                                scanner.process(image)
                                    .addOnSuccessListener { barcodes ->
                                        barcodes.firstOrNull { it.format == Barcode.FORMAT_QR_CODE }
                                            ?.rawValue
                                            ?.let {
                                                scanned = true
                                                onQRDetected(it)
                                            }
                                    }
                                    .addOnCompleteListener { proxy.close() }
                            } else {
                                proxy.close()
                            }
                        }
                    }
                runCatching {
                    provider.unbindAll()
                    provider.bindToLifecycle(lifecycleOwner, androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA, preview, analysis)
                }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        }
    )
}