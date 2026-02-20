package cam.lucane.studio.log.rpg.ui.components.common

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.ui.theme.*
import cam.lucane.studio.log.rpg.ui.utils.coloredShadow
import coil.compose.AsyncImage
import java.io.File

@Composable
fun ProfileImage(
    characterName: String,
    accentBrush: Brush,
    imagePath: String?,
    size: Dp = 80.dp,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier.coloredShadow(
            color = Color.Black.copy(0.1f),
            borderRadius = 20.dp,
            blurRadius = 12.dp,
            offsetY = 4.dp
        )
    ) {
        Box(
            modifier = modifier
                .size(size)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    accentBrush
                )
                .then(
                    if (onClick != null) {
                        Modifier.clickable { onClick() }
                    } else {
                        Modifier
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (imagePath != null && File(imagePath).exists()) {
                // Image personnalisée
                AsyncImage(
                    model = Uri.fromFile(File(imagePath)),
                    contentDescription = "Photo de $characterName",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = characterName.firstOrNull()?.uppercase() ?: "?",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = NunitoFontFamily,
                    color = Color.White
                )
            }
        }
    }
}