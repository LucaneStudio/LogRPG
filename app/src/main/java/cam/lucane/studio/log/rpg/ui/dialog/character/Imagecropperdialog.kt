package cam.lucane.studio.log.rpg.ui.dialog.character

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.theme.NunitoFontFamily
import coil.compose.AsyncImage

@Composable
fun ImageCropperDialog(
    imageUri: Uri,
    onDismiss: () -> Unit,
    onCropped: (Uri) -> Unit,
    context: Context
) {
    var scale      by remember { mutableStateOf(1f) }
    var offset     by remember { mutableStateOf(Offset.Zero) }
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    var isCropping by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnClickOutside = false)
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0D0F1A))) {

            // ── Image + gestes ──
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            scale = (scale * zoom).coerceIn(0.5f, 5f)
                            val maxX = (canvasSize.width  * (scale - 1f) / 2f).coerceAtLeast(0f)
                            val maxY = (canvasSize.height * (scale - 1f) / 2f).coerceAtLeast(0f)
                            offset = Offset(
                                (offset.x + pan.x).coerceIn(-maxX, maxX),
                                (offset.y + pan.y).coerceIn(-maxY, maxY)
                            )
                        }
                    }
                    .onGloballyPositioned { canvasSize = it.size }
            ) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(scaleX = scale, scaleY = scale, translationX = offset.x, translationY = offset.y),
                    contentScale = ContentScale.Fit
                )
                CropOverlay(modifier = Modifier.fillMaxSize())
            }

            // ── Header ──
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(Color.Black.copy(.75f), Color.Transparent)))
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Bouton Fermer
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(.12f))
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Close, "Fermer", tint = Color.White, modifier = Modifier.size(20.dp))
                }

                // Titre centré
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Recadrer la photo",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = NunitoFontFamily,
                        color = Color.White
                    )
                    Text(
                        "Pincez pour zoomer · Glissez pour déplacer",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = NunitoFontFamily,
                        color = Color.White.copy(.5f)
                    )
                }
            }

            // ── Footer ──
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(.85f))))
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                // Réinitialiser
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clip(RoundedCornerShape(99.dp))
                        .background(Color.White.copy(.12f))
                        .border(1.dp, Color.White.copy(.2f), RoundedCornerShape(99.dp))
                        .clickable { scale = 1f; offset = Offset.Zero }
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        "Réinitialiser",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = NunitoFontFamily,
                        color = Color.White.copy(.8f)
                    )
                }

                // Valider
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clip(RoundedCornerShape(99.dp))
                        .background(
                            if (!isCropping) ColorsSystem.GradientGreen
                            else Brush.linearGradient(listOf(Color.White.copy(.15f), Color.White.copy(.15f)))
                        )
                        .clickable(enabled = !isCropping) { isCropping = true }
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (isCropping) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                        Text(
                            if (isCropping) "Traitement…" else "Valider",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = NunitoFontFamily,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }

    if (isCropping) {
        LaunchedEffect(Unit) {
            val result = cropImageToCircle(context, imageUri, scale, offset, canvasSize)
            if (result != null) onCropped(result) else isCropping = false
        }
    }
}
