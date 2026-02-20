package cam.lucane.studio.log.rpg.ui.screen.detail.tabs.counters

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.ui.components.common.buttons.CardOptionButton
import cam.lucane.studio.log.rpg.ui.components.common.buttons.ControlButton
import cam.lucane.studio.log.rpg.ui.components.common.buttons.SmallIconBtn
import cam.lucane.studio.log.rpg.ui.theme.*
import cam.lucane.studio.log.rpg.ui.utils.coloredShadow

@Composable
fun StatCounterCard(
    label: String,
    current: Int,
    max: Int,
    mainColor: Color,
    backgroundMainColor:  Color,
    mainBrush: Brush,
    temporaryLabel: String?,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    onReset: () -> Unit,
    onEditMax: () -> Unit,
    onEditMode: (() -> Unit)? = null
) {
    val progress = if (max > 0) (current.toFloat() / max).coerceIn(0f, 1f) else 0f
    val isAtMax = current == max

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = ColorsSystem.BackgroundCard),
        modifier = Modifier
            .fillMaxWidth()
            .coloredShadow(
                color = ColorsSystem.Shadow.copy(0.08f),
                borderRadius = 22.dp,
                blurRadius = 16.dp,
                offsetY = 4.dp
            )
    ) {
        Box {

            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = TextSecondary,
                        letterSpacing = 1.5.sp,
                        fontFamily = NunitoFontFamily
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        // Reset
                        CardOptionButton(
                            modifier = Modifier.size(26.dp),
                            onClick = onReset,
                            color = mainColor
                        ) {
                            Text(
                                text = "💤",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = NunitoFontFamily
                            )
                        }
                        // Edit max
                        CardOptionButton(
                            modifier = Modifier.size(26.dp),
                            onClick = onEditMax,
                            color = mainColor
                        ) {
                            Text(
                                text = "✏️",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = NunitoFontFamily
                            )
                        }
                        onEditMode?.let { action ->
                            CardOptionButton(
                                modifier = Modifier.size(26.dp),
                                onClick = action,
                                color = mainColor
                            ) {
                                Text(
                                    text = "⚙️",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = NunitoFontFamily
                                )
                            }
                        }
                    }
                }

                // Barre de progression
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(CircleShape)
                        .background(ColorsSystem.Divider)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(progress)
                            .clip(CircleShape)
                            .background(
                                mainBrush
                            )
                    )
                }

                // Valeur + contrôles
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Bouton -
                    ControlButton(
                        onClick = onMinus,
                        containerColor = backgroundMainColor,
                    ) {
                        Text(
                            text = "-",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = mainColor,
                            fontFamily = NunitoFontFamily
                        )
                    }

                    // Valeur
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$current",
                            fontSize = 36.sp,
                            lineHeight = 38.sp,
                            fontWeight = FontWeight.Black,
                            color = mainColor,
                            fontFamily = NunitoFontFamily
                        )
                        Text(
                            text = "/ $max",
                            fontSize = 13.sp,
                            lineHeight = 14.sp,
                            color = ColorsSystem.TextDisabled,
                            fontWeight = FontWeight.Bold,
                            fontFamily = NunitoFontFamily
                        )
                    }

                    // Bouton +
                    ControlButton(
                        onClick = onPlus,
                        containerColor = backgroundMainColor,
                    ) {
                        Text(
                            text = "+",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = mainColor,
                            fontFamily = NunitoFontFamily
                        )
                    }
                }

                // Label PV temporaires
                temporaryLabel?.let {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = it,
                        fontSize = 11.sp,
                        color = mainColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}