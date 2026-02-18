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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.ui.theme.*
import coil.compose.AsyncImage

@Composable
fun ProfileImagePicker(
    currentImageUri: Uri?,
    onDismiss: () -> Unit,
    onImageSelected: (Uri?) -> Unit
) {
    var pickedUri by remember { mutableStateOf<Uri?>(null) }
    var showCropper by remember { mutableStateOf(false) }
    var selectedUri by remember { mutableStateOf(currentImageUri) }
    val context = LocalContext.current

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { pickedUri = it; showCropper = true }
    }

    if (showCropper && pickedUri != null) {
        ImageCropperDialog(
            imageUri = pickedUri!!,
            onDismiss = { showCropper = false },
            onCropped = { croppedUri -> selectedUri = croppedUri; showCropper = false },
            context = context
        )
        return
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Photo de profil", color = TextPrimary) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ProfileImagePreview(
                    uri = selectedUri,
                    onClick = { imagePicker.launch("image/*") }
                )
                Text(
                    if (selectedUri != null) "Appuyez pour changer ou recadrer" else "Appuyez pour choisir une photo",
                    fontSize = 12.sp, color = TextSecondary
                )
                if (selectedUri != null) {
                    OutlinedButton(
                        onClick = { selectedUri = null },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = HealthRed)
                    ) {
                        Icon(Icons.Default.Delete, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Retirer la photo", fontSize = 13.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onImageSelected(selectedUri) },
                colors = ButtonDefaults.buttonColors(containerColor = AccentPurple)
            ) { Text("Valider") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler", color = TextSecondary) }
        },
        containerColor = SurfaceDark,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
private fun ProfileImagePreview(uri: Uri?, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(140.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Brush.radialGradient(listOf(AccentPurple.copy(alpha = 0.2f), Color.Transparent)))
            .border(3.dp, BorderSubtle, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (uri != null) {
            AsyncImage(
                model = uri,
                contentDescription = "Aperçu",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.35f)),
                contentAlignment = Alignment.Center
            ) { Text("Changer", color = Color.White, fontSize = 13.sp) }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.AddAPhoto, null, tint = AccentPurple, modifier = Modifier.size(48.dp))
                Text("Choisir", fontSize = 13.sp, color = AccentPurple)
            }
        }
    }
}