package cam.lucane.studio.log.rpg.ui.dialog.character

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cam.lucane.studio.log.rpg.ui.theme.*
import coil.compose.AsyncImage

@Composable
fun ImageCropperDialog(
    imageUri: Uri,
    onDismiss: () -> Unit,
    onCropped: (Uri) -> Unit,
    context: Context
) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    var isCropping by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnClickOutside = false)
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {

            // Image avec gestes zoom/pan
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            scale = (scale * zoom).coerceIn(0.5f, 5f)
                            val maxX = (canvasSize.width * (scale - 1f) / 2f).coerceAtLeast(0f)
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

            CropperHeader(onDismiss = onDismiss, modifier = Modifier.align(Alignment.TopCenter))
            CropperFooter(
                isCropping = isCropping,
                onReset = { scale = 1f; offset = Offset.Zero },
                onValidate = { isCropping = true },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }

    if (isCropping) {
        LaunchedEffect(Unit) {
            val result = cropImageToCircle(context, imageUri, scale, offset, canvasSize)
            if (result != null) onCropped(result) else isCropping = false
        }
    }
}

@Composable
private fun CropperHeader(onDismiss: () -> Unit, modifier: Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(Color.Black.copy(0.7f), Color.Transparent)))
            .padding(16.dp)
    ) {
        IconButton(onClick = onDismiss, modifier = Modifier.align(Alignment.CenterStart)) {
            Icon(Icons.Default.Close, "Annuler", tint = Color.White)
        }
        Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Recadrer la photo", color = Color.White, fontSize = 16.sp)
            Text("Pincez pour zoomer · Glissez pour déplacer", color = Color.White.copy(0.6f), fontSize = 11.sp)
        }
    }
}

@Composable
private fun CropperFooter(
    isCropping: Boolean,
    onReset: () -> Unit,
    onValidate: () -> Unit,
    modifier: Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.8f))))
            .padding(24.dp)
    ) {
        TextButton(onClick = onReset, modifier = Modifier.align(Alignment.CenterStart)) {
            Text("Réinitialiser", color = Color.White.copy(0.7f))
        }
        Button(
            onClick = onValidate,
            modifier = Modifier.align(Alignment.CenterEnd),
            colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
            enabled = !isCropping
        ) {
            if (isCropping) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
            } else {
                Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Valider")
            }
        }
    }
}