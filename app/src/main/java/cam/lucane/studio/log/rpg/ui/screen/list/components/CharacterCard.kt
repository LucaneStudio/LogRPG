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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.room.util.TableInfo
import cam.lucane.studio.log.rpg.data.entity.Character
import cam.lucane.studio.log.rpg.data.entity.ManaMode
import cam.lucane.studio.log.rpg.data.entity.SpellSlot
import cam.lucane.studio.log.rpg.data.entity.getSpellSlots
import cam.lucane.studio.log.rpg.ui.components.common.ProfileImage
import cam.lucane.studio.log.rpg.ui.screen.list.components.bar.HealthManaBar
import cam.lucane.studio.log.rpg.ui.screen.list.components.bar.SpellSlotsBar
import cam.lucane.studio.log.rpg.ui.theme.AccentGold
import cam.lucane.studio.log.rpg.ui.theme.AccentGreen
import cam.lucane.studio.log.rpg.ui.theme.AccentPurple
import cam.lucane.studio.log.rpg.ui.theme.AccentRed
import cam.lucane.studio.log.rpg.ui.theme.BorderSubtle
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.theme.GlassSurface
import cam.lucane.studio.log.rpg.ui.theme.HealthRed
import cam.lucane.studio.log.rpg.ui.theme.ManaBlue
import cam.lucane.studio.log.rpg.ui.theme.NunitoFontFamily
import cam.lucane.studio.log.rpg.ui.theme.TextPrimary
import cam.lucane.studio.log.rpg.ui.theme.TextSecondary
import cam.lucane.studio.log.rpg.ui.utils.coloredShadow
import cam.lucane.studio.log.rpg.ui.utils.getAccentBrushByCharacterId
import cam.lucane.studio.log.rpg.ui.utils.getAccentColorByCharacterId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterCard(
    character: Character,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    // Couleur d'accent basée sur le nom (pour différencier les cartes)
    val accentColor = remember(character.id) {
        getAccentColorByCharacterId(character.id)
    }

    val accentBrush = remember(character.id) {
        getAccentBrushByCharacterId(character.id)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .coloredShadow(
                color = ColorsSystem.Shadow.copy(0.08f),
                borderRadius = 22.dp,
                blurRadius = 16.dp,
                offsetY = 4.dp
            ),
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = ColorsSystem.BackgroundCard, contentColor = accentColor),
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ProfileImage(
                characterName = character.name,
                imagePath = character.profileImagePath,
                size = 65.dp,
                accentBrush = accentBrush
            )
            Column() {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = character.name,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ColorsSystem.TextPrimary,
                        fontFamily = NunitoFontFamily
                    )

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(20.dp),
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            "Supprimer",
                            tint = TextSecondary.copy(alpha = 0.5f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(Modifier.size(5.dp))
                HealthManaBar(
                    label = "❤\uFE0F",
                    current = character.currentHealth,
                    max = character.maxHealth,
                    color = ColorsSystem.Red
                )

                if (character.manaMode == ManaMode.MANA) {
                    HealthManaBar(
                        label = "\uD83D\uDCA7",
                        current = character.currentMana,
                        max = character.maxMana,
                        color = ColorsSystem.Blue
                    )
                }
                else {
                    val activeSlots = character.getSpellSlots().filter { it.max > 0 }
                    SpellSlotsBar(
                        label = "📖",
                        slots = activeSlots
                    )
                }
            }
        }
    }
}

