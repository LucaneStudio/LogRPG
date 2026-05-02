package cam.lucane.studio.log.rpg.ui.dialog.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.theme.NunitoFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseBottomSheet(
    onDismiss: () -> Unit,
    title    : String,
    subtitle : String? = null,
    content  : @Composable ColumnScope.() -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor   = ColorsSystem.BackgroundCard,
        // Handle identique à l'ancien design
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 14.dp)
                    .width(36.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(ColorsSystem.Divider)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(bottom = 20.dp)
        ) {
            Text(
                text       = title,
                fontSize   = 17.sp,
                fontWeight = FontWeight.Black,
                fontFamily = NunitoFontFamily,
                color      = ColorsSystem.TextPrimary
            )

            if (subtitle != null) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text       = subtitle,
                    fontSize   = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = NunitoFontFamily,
                    color      = ColorsSystem.TextDisabled
                )
            }

            Spacer(Modifier.height(20.dp))

            content()
        }
    }
}