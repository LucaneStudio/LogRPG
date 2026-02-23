package cam.lucane.studio.log.rpg.ui.dialog.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.theme.NunitoFontFamily

@Composable
fun BaseBottomSheet(
    onDismiss: () -> Unit,
    title: String,
    subtitle: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .background(ColorsSystem.BackgroundCard)
                    .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 36.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                // Handle
                Box(
                    modifier = Modifier
                        .width(36.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(ColorsSystem.Divider)
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(Modifier.height(18.dp))

                Text(
                    text = title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = NunitoFontFamily,
                    color = ColorsSystem.TextPrimary
                )

                if (subtitle != null) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = NunitoFontFamily,
                        color = ColorsSystem.TextDisabled
                    )
                }

                Spacer(Modifier.height(20.dp))

                content()
            }
        }
    }
}
