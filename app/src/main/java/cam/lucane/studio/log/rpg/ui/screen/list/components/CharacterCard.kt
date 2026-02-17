package cam.lucane.studio.log.rpg.ui.screen.list.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.data.entity.Character
import cam.lucane.studio.log.rpg.ui.theme.AccentGold
import cam.lucane.studio.log.rpg.ui.theme.AccentGreen
import cam.lucane.studio.log.rpg.ui.theme.AccentPurple
import cam.lucane.studio.log.rpg.ui.theme.AccentRed
import cam.lucane.studio.log.rpg.ui.theme.BorderSubtle
import cam.lucane.studio.log.rpg.ui.theme.GlassSurface
import cam.lucane.studio.log.rpg.ui.theme.HealthRed
import cam.lucane.studio.log.rpg.ui.theme.ManaBlue
import cam.lucane.studio.log.rpg.ui.theme.TextPrimary
import cam.lucane.studio.log.rpg.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterCard(
    character: Character,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    // Couleur d'accent basée sur le nom (pour différencier les cartes)
    val accentColor = remember(character.id) {
        val colors = listOf(AccentRed, AccentPurple, AccentGreen, AccentGold)
        colors[(character.id % colors.size).toInt()]
    }

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GlassSurface),
        border = BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Orbe d'ambiance dans la carte
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 30.dp, y = (-20).dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(accentColor.copy(alpha = 0.2f), Color.Transparent)
                        )
                    )
            )

            Column(modifier = Modifier.padding(16.dp)) {
                // Header : Avatar + nom + bouton supprimer
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(accentColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = character.name.firstOrNull()?.toString() ?: "?",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = accentColor
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = character.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Appuyer pour ouvrir",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            "Supprimer",
                            tint = TextSecondary.copy(alpha = 0.5f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Barres PV et Mana
                HealthManaBar(
                    label = "PV",
                    current = character.currentHealth,
                    max = character.maxHealth,
                    color = HealthRed
                )
                Spacer(modifier = Modifier.height(6.dp))
                HealthManaBar(
                    label = "PM",
                    current = character.currentMana,
                    max = character.maxMana,
                    color = ManaBlue
                )
            }
        }
    }
}

@Composable
private fun HealthManaBar(
    label: String,
    current: Int,
    max: Int,
    color: Color
) {
    val progress = if (max > 0) (current.toFloat() / max).coerceIn(0f, 1f) else 0f

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            color = color,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.width(20.dp)
        )

        Box(
            modifier = Modifier
                .weight(1f)
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
                            colors = listOf(color, color.copy(alpha = 0.7f))
                        )
                    )
            )
        }

        Text(
            text = "$current/$max",
            fontSize = 10.sp,
            color = TextSecondary,
            modifier = Modifier.width(40.dp),
            textAlign = TextAlign.End
        )
    }
}