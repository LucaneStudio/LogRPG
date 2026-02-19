package cam.lucane.studio.log.rpg.ui.screen.detail.tabs.sheet

import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cam.lucane.studio.log.rpg.ui.components.pdf.PdfViewer
import cam.lucane.studio.log.rpg.ui.theme.AccentPurple
import cam.lucane.studio.log.rpg.ui.theme.TextPrimary
import cam.lucane.studio.log.rpg.ui.theme.TextSecondary
import cam.lucane.studio.log.rpg.ui.viewmodel.CharacterDetailViewModel

@Composable
fun SheetTab(
    character: cam.lucane.studio.log.rpg.data.entity.Character,
    viewModel: CharacterDetailViewModel,
    pdfLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    onImportPdf: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        val pdfFile = character.pdfPath?.let { java.io.File(it) }

        if (pdfFile != null && pdfFile.exists()) {
            PdfViewer(
                pdfFile = pdfFile,
                pdfLauncher = pdfLauncher,
                characterId = character.id
            )
        } else {
            // État vide
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("📄", fontSize = 64.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Aucune fiche PDF",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Importez votre fiche de personnage",
                    fontSize = 14.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onImportPdf,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentPurple)
                ) {
                    Icon(Icons.Default.Upload, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Importer un PDF")
                }
            }
        }
    }
}