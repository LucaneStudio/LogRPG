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
import coil.compose.AsyncImage
import java.io.File

@Composable
fun ProfileImage(
    characterName: String,
    imagePath: String?,
    color: Color,
    size: Dp = 80.dp,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val borderWidth = (size.value / 30).dp

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        color.copy(alpha = 0.3f),
                        color.copy(alpha = 0.1f)
                    )
                )
            )
            .border(borderWidth, BorderSubtle, RoundedCornerShape(14.dp))
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
            // Fallback : initiale + icône
            Icon(
                Icons.Default.Person,
                contentDescription = null,
                tint = color.copy(alpha = 0.3f),
                modifier = Modifier.size(size * 0.6f)
            )

            Text(
                text = characterName.firstOrNull()?.uppercase() ?: "?",
                fontSize = (size.value / 2.5).sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}