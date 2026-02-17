package cam.lucane.studio.log.rpg.ui.components.common

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.ui.theme.TextSecondary

@Composable
fun EmptyState(
    emoji: String = "🔍",
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(emoji, fontSize = 48.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Text(message, fontSize = 15.sp, color = TextSecondary)
    }
}