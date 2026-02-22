package cam.lucane.studio.log.rpg.ui.screen.detail.tabs.notes.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.data.entity.Note
import cam.lucane.studio.log.rpg.ui.components.common.buttons.CardOptionButton
import cam.lucane.studio.log.rpg.ui.theme.*
import cam.lucane.studio.log.rpg.ui.utils.coloredShadow
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NoteCard(
    note: Note,
    mainColor: Color,
    onClick: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy · HH:mm", Locale.FRENCH)
    val updatedDate = dateFormat.format(Date(note.updatedAt))

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(contentColor = mainColor, containerColor = ColorsSystem.BackgroundCard),
        modifier = Modifier
            .fillMaxWidth()
            .coloredShadow(
                color = ColorsSystem.Shadow.copy(0.08f),
                borderRadius = 22.dp,
                blurRadius = 16.dp,
                offsetY = 4.dp
            )
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick.invoke() }
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp, bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = note.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = ColorsSystem.TextPrimary,
                    fontFamily = NunitoFontFamily,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    CardOptionButton(
                        modifier = Modifier.size(26.dp),
                        onClick = onRename,
                        color = mainColor
                    ) {
                        Text(
                            text = "✏️",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = NunitoFontFamily
                        )
                    }

                    CardOptionButton(
                        modifier = Modifier.size(26.dp),
                        onClick = onDelete,
                        color = mainColor
                    ) {
                        Text(
                            text = "🗑️",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = NunitoFontFamily
                        )
                    }
                }
            }

            if (note.content.isNotBlank()) {
                Text(
                    text = note.content,
                    fontSize = 13.sp,
                    color = ColorsSystem.TextSecondary,
                    fontFamily = NunitoFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )
            } else {
                Text(
                    text = "Note vide...",
                    fontSize = 13.sp,
                    fontFamily = NunitoFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = ColorsSystem.TextSecondary.copy(alpha = 0.5f)
                )
            }

            Text(
                text = "Modifiée le $updatedDate",
                fontSize = 11.sp,
                letterSpacing = 0.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = ColorsSystem.TextDisabled,
                fontFamily = NunitoFontFamily,
            )
        }
    }
}