package cam.lucane.studio.log.rpg.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.theme.NunitoFontFamily

data class DropdownAction(
    val emoji: String,
    val label: String,
    val isDanger: Boolean = false,
    val onClick: () -> Unit
)

@Composable
fun CharacterSettingsDropdown(
    expanded: Boolean,
    onDismiss: () -> Unit,
    actions: List<DropdownAction>
) {
    if (!expanded) return

    Popup(
        offset = IntOffset(x = 35, y = 120),
        alignment = Alignment.TopEnd,
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = true)
    ) {
        Box(
            modifier = Modifier
                .padding(top = 4.dp, end = 14.dp)
                .shadow(16.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(ColorsSystem.BackgroundCard)
                .width(200.dp)
        ) {
            Column {
                actions.forEachIndexed { index, action ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onDismiss()
                                action.onClick()
                            }
                            .padding(horizontal = 14.dp, vertical = 13.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(action.emoji, fontSize = 16.sp)
                        Text(
                            text = action.label,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = NunitoFontFamily,
                            color = if (action.isDanger) ColorsSystem.Red else ColorsSystem.TextPrimary
                        )
                    }

                    // Divider sauf après le dernier
                    if (index < actions.size - 1) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(ColorsSystem.Divider)
                        )
                    }
                }
            }
        }
    }
}
