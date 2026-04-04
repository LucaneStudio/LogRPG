package cam.lucane.studio.log.rpg.ui.screen.detail.tabs.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.ui.combat.components.common.CombatTextField
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSectionBottomSheet(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var title by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor   = ColorsSystem.BackgroundCard,
        dragHandle = {
            Box(
                Modifier.padding(vertical = 14.dp)
                    .width(36.dp).height(4.dp)
                    .clip(RoundedCornerShape(99.dp))
                    .background(ColorsSystem.SecondBorder)
            )
        },
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp).padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text("Nouvelle section", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = ColorsSystem.TextPrimary)

            CombatTextField(
                value         = title,
                onValueChange = { title = it },
                label         = "Nom (ex : Magie, Combat…)",
                modifier      = Modifier.fillMaxWidth(),
            )

            Button(
                onClick  = { onConfirm(title) },
                enabled  = title.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(99.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = ColorsSystem.Green),
            ) {
                Text("Créer la section", fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(vertical = 4.dp))
            }
        }
    }
}
