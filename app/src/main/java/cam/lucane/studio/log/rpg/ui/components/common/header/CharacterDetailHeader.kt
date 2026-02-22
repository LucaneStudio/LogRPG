package cam.lucane.studio.log.rpg.ui.components.common.header

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.data.entity.Character
import cam.lucane.studio.log.rpg.ui.components.common.FilterChip
import cam.lucane.studio.log.rpg.ui.components.common.ProfileImage
import cam.lucane.studio.log.rpg.ui.screen.detail.tabs.inventory.ItemFilter
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.theme.NunitoFontFamily
import cam.lucane.studio.log.rpg.ui.utils.coloredShadow
import cam.lucane.studio.log.rpg.ui.utils.getAccentBrushByCharacterId
import cam.lucane.studio.log.rpg.ui.utils.getAccentColorByCharacterId
import kotlin.collections.count

@Composable
fun CharacterDetailHeader(
    modifier: Modifier = Modifier,
    character: Character,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onBackClick: () -> Unit,
    settingsButton: @Composable () -> Unit,
) {
    // Couleur d'accent basée sur le nom (pour différencier les cartes)
    val accentColor = remember(character.id) {
        getAccentColorByCharacterId(character.id)
    }

    val accentBrush = remember(character.id) {
        getAccentBrushByCharacterId(character.id)
    }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // ── Header ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Bouton retour
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .coloredShadow(ColorsSystem.Shadow.copy(0.08f), 12.dp, 10.dp, offsetY = 3.dp)
                    .background(ColorsSystem.BackgroundCard, RoundedCornerShape(12.dp))
                    .clickable { onBackClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    "Précédent",
                    modifier = Modifier.size(20.dp),
                    tint = ColorsSystem.TextSecondary
                )
            }

            ProfileImage(
                characterName = character.name,
                imagePath = character.profileImagePath,
                size = 46.dp,
                accentBrush = accentBrush
            )

            // Nom
            Text(
                text = character.name,
                fontSize = 17.sp,
                fontWeight = FontWeight.Black,
                fontFamily = NunitoFontFamily,
                color = ColorsSystem.TextPrimary,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Bouton settings
            settingsButton.invoke()
        }

        // ── Tab pills ──
        val tabs = listOf( "📄 Fiche", "🎲 Compt.", "📖 Sorts", "🎒 Inventaire", "📝 Notes")

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            itemsIndexed(tabs) { index, label ->
                val isActive = selectedTab == index

                FilterChip(
                    label = label,
                    selected = isActive,
                    color = accentColor,
                    onClick = { onTabSelected(index) }
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
    }
}