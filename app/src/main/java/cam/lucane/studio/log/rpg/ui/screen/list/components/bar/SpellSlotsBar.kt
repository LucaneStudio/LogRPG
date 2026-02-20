package cam.lucane.studio.log.rpg.ui.screen.list.components.bar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.data.entity.SpellSlot
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.theme.NunitoFontFamily
import kotlin.collections.forEach

@Composable
fun SpellSlotsBar(
    label: String,
    slots: List<SpellSlot> // seulement les niveaux avec max > 0
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = ColorsSystem.Purple,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.width(20.dp),
            fontFamily = NunitoFontFamily
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            slots.forEach { slot ->
                val isEmpty = slot.current == 0
                Text(
                    text = slot.current.toString(),
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = NunitoFontFamily,
                    color = if (isEmpty) ColorsSystem.TextDisabled else ColorsSystem.Blue,
                    modifier = Modifier
                        .background(
                            color = if (isEmpty) ColorsSystem.Divider else ColorsSystem.BlueLight,
                            shape = CircleShape
                        )
                        .padding(horizontal = 5.dp)
                )
            }
        }
    }
}