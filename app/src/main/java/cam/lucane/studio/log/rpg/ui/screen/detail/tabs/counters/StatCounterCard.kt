package cam.lucane.studio.log.rpg.ui.screen.detail.tabs.counters

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.data.entity.Character
import cam.lucane.studio.log.rpg.data.entity.CurrencyMode
import cam.lucane.studio.log.rpg.data.entity.getCurrencyDisplay
import cam.lucane.studio.log.rpg.ui.components.common.ControlButton
import cam.lucane.studio.log.rpg.ui.components.common.SmallIconBtn
import cam.lucane.studio.log.rpg.ui.theme.*
import cam.lucane.studio.log.rpg.ui.viewmodel.CharacterDetailViewModel

@Composable
fun StatCounterCard(
    label: String,
    current: Int,
    max: Int,
    accentColor: Color,
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
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GlassSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box {

            Column(modifier = Modifier.padding(16.dp)) {
                // Ligne titre + boutons utilitaires
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary,
                        letterSpacing = 1.5.sp
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        // Reset
                        SmallIconBtn(
                            onClick = onReset,
                            enabled = !isAtMax,
                            tint = if (!isAtMax) AccentGreen else TextSecondary.copy(alpha = 0.3f)
                        ) {
                            Icon(Icons.Default.Refresh, "Réinitialiser", modifier = Modifier.size(16.dp))
                        }
                        // Edit max
                        SmallIconBtn(onClick = onEditMax) {
                            Icon(Icons.Default.Edit, "Modifier max", modifier = Modifier.size(16.dp))
                        }
                        onEditMode?.let { action ->
                            SmallIconBtn(onClick = action) {
                                Icon(Icons.Default.Settings, "Changer de mode", modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Valeur + contrôles
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Bouton -
                    ControlButton(
                        onClick = onMinus,
                        color = accentColor.copy(alpha = 0.15f),
                        borderColor = accentColor.copy(alpha = 0.3f)
                    ) {
                        Icon(Icons.Default.Remove, "Moins", tint = accentColor, modifier = Modifier.size(18.dp))
                    }

                    // Valeur
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "$current",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = accentColor
                        )
                        Text(
                            text = "/ $max",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }

                    // Bouton +
                    ControlButton(
                        onClick = onPlus,
                        color = accentColor.copy(alpha = 0.15f),
                        borderColor = accentColor.copy(alpha = 0.3f)
                    ) {
                        Icon(Icons.Default.Add, "Plus", tint = accentColor, modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Barre de progression
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White.copy(alpha = 0.07f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(progress)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(accentColor, accentColor.copy(alpha = 0.6f))
                                )
                            )
                    )
                }

                // Label PV temporaires
                temporaryLabel?.let {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = it,
                        fontSize = 11.sp,
                        color = accentColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}