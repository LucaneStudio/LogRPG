package cam.lucane.studio.log.rpg.ui.components.common.header

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.R
import cam.lucane.studio.log.rpg.ui.components.common.DropdownInputs
import cam.lucane.studio.log.rpg.ui.components.common.DropdownAction
import cam.lucane.studio.log.rpg.ui.theme.*
import cam.lucane.studio.log.rpg.ui.utils.coloredShadow

@Composable
fun HomeHeader(
    onImportClick: () -> Unit,
    onCreateSession: () -> Unit,
    onJoinSession: () -> Unit,
    isInSession: Boolean = false,
    modifier: Modifier = Modifier
) {
    var showSessionMenu by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ── Logo pill ────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .coloredShadow(
                    color = ColorsSystem.Shadow.copy(0.08f),
                    borderRadius = 99.dp,
                    blurRadius = 12.dp,
                    offsetY = 3.dp
                )
                .background(ColorsSystem.BackgroundCard, CircleShape)
                .padding(end = 20.dp, start = 12.dp, top = 7.dp, bottom = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Box(Modifier.size(28.dp), contentAlignment = Alignment.Center) {
                Image(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(R.drawable.ic_logrpg_monograme),
                    contentDescription = "LogRPG logo"
                )
            }
            androidx.compose.material3.Icon(
                modifier = Modifier
                    .size(width = 52.dp, height = 14.dp)
                    .offset(y = 1.dp),
                painter = painterResource(R.drawable.ic_logrpg_typo),
                tint = ColorsSystem.TextPrimary,
                contentDescription = "LogRPG"
            )
        }

        // ── Boutons droite ───────────────────────────────────────────
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            if(!isInSession) {
                Box {
                    Row(
                        modifier = Modifier
                            .coloredShadow(
                                color = ColorsSystem.Shadow.copy(0.08f),
                                borderRadius = 99.dp,
                                blurRadius = 10.dp,
                                offsetY = 3.dp
                            )
                            .background(
                                ColorsSystem.BackgroundCard,
                                RoundedCornerShape(99.dp)
                            )
                            .clickable { showSessionMenu = true }
                            .padding(horizontal = 12.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(text = "🎲", fontSize = 14.sp)
                        Text(
                            text = if (showSessionMenu) "Session  ∧" else "Session  ∨",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ColorsSystem.TextSecondary,
                            fontFamily = NunitoFontFamily
                        )
                    }

                    // Dropdown session
                    DropdownInputs(
                        expanded = showSessionMenu,
                        onDismiss = { showSessionMenu = false },
                        actions = listOf(
                            DropdownAction(
                                emoji = "🎲",
                                label = "Créer une session"
                            ) { onCreateSession() },
                            DropdownAction(
                                emoji = "📷",
                                label = "Rejoindre une session"
                            ) { onJoinSession() }
                        )
                    )
                }
            }
            // Import JSON
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .coloredShadow(
                        color = ColorsSystem.Shadow.copy(0.08f),
                        borderRadius = 99.dp,
                        blurRadius = 10.dp,
                        offsetY = 3.dp
                    )
                    .background(ColorsSystem.BackgroundCard, CircleShape)
                    .clickable { onImportClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "📥", fontSize = 16.sp)
            }
        }
    }
}