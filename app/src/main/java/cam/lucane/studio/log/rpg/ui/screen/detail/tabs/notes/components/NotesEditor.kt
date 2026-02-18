package cam.lucane.studio.log.rpg.ui.screen.detail.tabs.notes.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.ui.theme.*

@Composable
fun NotesEditor(
    notes: String,
    onNotesChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        shape = RoundedCornerShape(16.dp),
        color = GlassSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.02f),
                            Color.Transparent
                        )
                    )
                )
        ) {
            OutlinedTextField(
                value = notes,
                onValueChange = onNotesChange,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
                placeholder = {
                    Text(
                        "# Mon Personnage\n\n" +
                                "## Historique\n" +
                                "**Nom complet** : Jean d'Arc\n" +
                                "*Né en* : Lunargent, 1402\n\n" +
                                "## Objectifs\n" +
                                "- Retrouver l'Épée Perdue\n" +
                                "- Vaincre le Dragon\n" +
                                "- Sauver le royaume\n\n" +
                                "> \"Le courage n'est pas l'absence de peur\"\n\n" +
                                "## Secrets\n" +
                                "`Code d'accès` : 7734\n\n" +
                                "### Syntaxe Markdown\n" +
                                "# Titre 1\n" +
                                "## Titre 2\n" +
                                "### Titre 3\n" +
                                "**Gras** ou *Italique*\n" +
                                "- Liste à puces\n" +
                                "> Citation\n" +
                                "`Code inline`",
                        color = TextSecondary.copy(alpha = 0.4f),
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = AccentPurple,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
            )
        }
    }
}