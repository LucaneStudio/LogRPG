package cam.lucane.studio.log.rpg.ui.screen.detail.tabs.notes.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.ui.theme.*

@Composable
fun NotesEditor(
    notes: String,
    mainColor: Color,
    onNotesChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = ColorsSystem.BackgroundCard,
        modifier = modifier.fillMaxSize(),
    ) {
        OutlinedTextField(
            value = notes,
            onValueChange = onNotesChange,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp),
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
                    color = ColorsSystem.TextDisabled,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontFamily = NunitoFontFamily,
                    fontWeight = FontWeight.SemiBold
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                cursorColor = mainColor,
                focusedTextColor = ColorsSystem.TextPrimary,
                unfocusedTextColor = ColorsSystem.TextPrimary
            ),
            textStyle = LocalTextStyle.current.copy(
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = NunitoFontFamily
            )
        )
    }
}
