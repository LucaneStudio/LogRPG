package cam.lucane.studio.log.rpg.ui.components.pdf

import android.content.Intent
import android.graphics.Bitmap
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.ui.theme.AccentPurple
import cam.lucane.studio.log.rpg.ui.theme.BackgroundDark
import cam.lucane.studio.log.rpg.ui.theme.BorderSubtle
import cam.lucane.studio.log.rpg.ui.theme.TextSecondary

@Composable
fun PdfPageImage(
    bitmap: Bitmap,
    mainColor: Color,
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
                        color = mainColor,
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
                        tint = mainColor
                    )
                    Text(
                        text = "Modifier",
                        fontSize = 11.sp,
                        color = mainColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

    }
}