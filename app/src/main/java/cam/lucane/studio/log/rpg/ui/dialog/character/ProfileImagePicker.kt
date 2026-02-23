package cam.lucane.studio.log.rpg.ui.dialog.character

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.data.entity.Character
import cam.lucane.studio.log.rpg.ui.dialog.common.BaseBottomSheet
import cam.lucane.studio.log.rpg.ui.dialog.common.SheetDivider
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.theme.NunitoFontFamily
import coil.compose.AsyncImage

@Composable
fun ProfileImagePicker(
    mainBrush: Brush,
    currentImageUri: Uri?,
    characterName: String,
    onDismiss: () -> Unit,
    onImageSelected: (Uri?) -> Unit
) {
    var pickedUri   by remember { mutableStateOf<Uri?>(null) }
    var showCropper by remember { mutableStateOf(false) }
    var selectedUri by remember { mutableStateOf(currentImageUri) }
    val context = LocalContext.current

    // Si le cropper est ouvert, on l'affiche à la place du sheet
    if (showCropper && pickedUri != null) {
        ImageCropperDialog(
            imageUri = pickedUri!!,
            onDismiss = { showCropper = false },
            onCropped = { croppedUri ->
                selectedUri = croppedUri
                showCropper = false
            },
            context = context
        )
        return
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { pickedUri = it; showCropper = true }
    }

    BaseBottomSheet(
        onDismiss = onDismiss,
        title = "Photo de profil",
        subtitle = if (selectedUri != null) "Choisir une source" else "Aucune photo définie"
    ) {
        // ── Aperçu avatar ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        brush = mainBrush
                    )
                    .border(3.dp, ColorsSystem.BackgroundCard, RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (selectedUri != null) {
                    AsyncImage(
                        model = selectedUri,
                        contentDescription = "Aperçu",
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(20.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(characterName[0].uppercase(), fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color.White, fontFamily = NunitoFontFamily)
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        SheetDivider()
        Spacer(Modifier.height(4.dp))

        SheetAction(
            emoji = "🖼️",
            emojiBackground = ColorsSystem.BlueLight,
            label = "Choisir depuis la galerie",
            desc = "Accéder à vos photos",
            onClick = { imagePicker.launch("image/*") }
        )

        if (selectedUri != null) {
            SheetDivider()

            SheetAction(
                emoji = "✂️",
                emojiBackground = ColorsSystem.GreenLight,
                label = "Recadrer la photo actuelle",
                desc = "Ajuster le cadrage et le zoom",
                onClick = { pickedUri = selectedUri; showCropper = true }
            )

            SheetAction(
                emoji = "🗑",
                emojiBackground = ColorsSystem.RedLight,
                label = "Supprimer la photo",
                desc = "Revenir à l'initiale colorée",
                labelColor = ColorsSystem.Red,
                onClick = { selectedUri = null }
            )
        }

        Spacer(Modifier.height(16.dp))

        // ── Boutons Annuler / Valider ──
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Annuler
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(99.dp))
                    .background(ColorsSystem.BackgroundSurface)
                    .border(1.5.dp, ColorsSystem.Divider, RoundedCornerShape(99.dp))
                    .clickable { onDismiss() }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Annuler", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, fontFamily = NunitoFontFamily, color = ColorsSystem.TextSecondary)
            }

            // Valider
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(99.dp))
                    .background(ColorsSystem.GradientGreen)
                    .clickable { onImageSelected(selectedUri) }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Valider", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, fontFamily = NunitoFontFamily, color = Color.White)
            }
        }
    }
}

// ── Composant ligne d'action ────────────────────────────────────────────

@Composable
private fun SheetAction(
    emoji: String,
    emojiBackground: Color,
    label: String,
    desc: String,
    labelColor: Color = ColorsSystem.TextPrimary,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(emojiBackground),
            contentAlignment = Alignment.Center
        ) {
            Text(emoji, fontSize = 19.sp)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, fontFamily = NunitoFontFamily, color = labelColor)
            Text(desc,  fontSize = 11.sp, fontWeight = FontWeight.SemiBold,  fontFamily = NunitoFontFamily, color = ColorsSystem.TextDisabled)
        }
    }
}
