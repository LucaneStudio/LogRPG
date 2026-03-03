package cam.lucane.studio.log.rpg.ui.screen.qr

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cam.lucane.studio.log.rpg.data.entity.Character
import cam.lucane.studio.log.rpg.ui.theme.ColorsSystem
import cam.lucane.studio.log.rpg.ui.theme.NunitoFontFamily
import cam.lucane.studio.log.rpg.ui.utils.coloredShadow
import cam.lucane.studio.log.rpg.ui.viewmodel.CharacterDetailViewModel
import cam.lucane.studio.log.rpg.utils.MultiQrCodeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// ─── Metadata de chaque QR ───────────────────
private data class QrMeta(
    val type: MultiQrCodeUtils.QrType,
    val label: String,
    val emoji: String,
    val color: androidx.compose.ui.graphics.Color,
    val colorLight: androidx.compose.ui.graphics.Color,
    val payload: String,
    var bitmap: Bitmap? = null
)

@Composable
fun MultiQrScreen(
    character: Character,
    viewModel: CharacterDetailViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // État des 4 QR
    var qrList by remember { mutableStateOf<List<QrMeta>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Vue : CAROUSEL ou GRID — grille en priorité
    var view by remember { mutableStateOf(QrView.GRID) }
    var currentIndex by remember { mutableStateOf(0) }

    // QR agrandi depuis la grille
    var zoomedQr by remember { mutableStateOf<QrMeta?>(null) }

    // Générer les 4 payloads au lancement
    LaunchedEffect(Unit) {
        withContext(Dispatchers.Default) {
            val list = viewModel.generateMultiQrPayloads()
            val metas = buildList {
                add(QrMeta(
                    MultiQrCodeUtils.QrType.STATS, "Stats & Compteurs", "🎲",
                    ColorsSystem.Purple, ColorsSystem.PurpleLight, list.stats
                ))
                add(QrMeta(
                    MultiQrCodeUtils.QrType.ABILITIES, "Sorts", "📖",
                    ColorsSystem.Blue, ColorsSystem.BlueLight, list.abilities
                ))
                add(QrMeta(
                    MultiQrCodeUtils.QrType.ITEMS, "Inventaire", "🎒",
                    ColorsSystem.Orange, ColorsSystem.OrangeLight, list.items
                ))
                add(QrMeta(
                    MultiQrCodeUtils.QrType.NOTES, "Notes", "📝",
                    ColorsSystem.Green, ColorsSystem.GreenLight, list.notes
                ))
            }
            // Générer les bitmaps
            val withBitmaps = metas.map { meta ->
                meta.copy(bitmap = MultiQrCodeUtils.generateBitmap(meta.payload, 600))
            }
            withContext(Dispatchers.Main) {
                qrList = withBitmaps
                isLoading = false
            }
        }
    }

    // ── Dialog QR agrandi ──────────────────────────────
    zoomedQr?.let { qr ->
        Dialog(
            onDismissRequest = { zoomedQr = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ColorsSystem.TextPrimary.copy(.85f))
                    .clickable { zoomedQr = null },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(24.dp)
                ) {
                    // Badge type
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(99.dp))
                            .background(qr.colorLight)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(qr.emoji, fontSize = 16.sp)
                        Text(qr.label, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold,
                            fontFamily = NunitoFontFamily, color = qr.color)
                    }

                    // QR agrandi
                    Box(
                        modifier = Modifier
                            .size(300.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(ColorsSystem.BackgroundCard)
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        qr.bitmap?.let {
                            Image(bitmap = it.asImageBitmap(),
                                contentDescription = qr.label,
                                modifier = Modifier.fillMaxSize())
                        }
                    }

                    Text("Appuyer n'importe où pour fermer",
                        fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                        fontFamily = NunitoFontFamily,
                        color = ColorsSystem.BackgroundCard.copy(.5f))
                }
            }
        }
    }

    // ── Écran principal ────────────────────────────────
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorsSystem.BackgroundApp)
    ) {
        // TopBar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(ColorsSystem.BackgroundCard)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .coloredShadow(ColorsSystem.Shadow.copy(.08f), 12.dp, 10.dp, offsetY = 3.dp)
                    .background(ColorsSystem.BackgroundCard, RoundedCornerShape(12.dp))
                    .clickable { onNavigateBack() },
                contentAlignment = Alignment.Center
            ) { Text("←", fontSize = 18.sp, color = ColorsSystem.TextSecondary) }

            Column(Modifier.weight(1f)) {
                Text("Multi-QR Codes", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold,
                    fontFamily = NunitoFontFamily, color = ColorsSystem.TextPrimary)
                Text("Faire scanner dans l'ordre par le Joueur B", fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold, fontFamily = NunitoFontFamily,
                    color = ColorsSystem.TextDisabled)
            }

            // Toggle vue
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(ColorsSystem.BackgroundSurface)
                    .border(1.5.dp, ColorsSystem.Divider, RoundedCornerShape(10.dp))
                    .clickable {
                        view = if (view == QrView.CAROUSEL) QrView.GRID else QrView.CAROUSEL
                    }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(if (view == QrView.CAROUSEL) "⊞" else "▶",
                    fontSize = 15.sp, color = ColorsSystem.TextSecondary)
            }
        }

        // Badge perso
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(ColorsSystem.BackgroundCard)
                .padding(horizontal = 16.dp).padding(bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier.size(32.dp).clip(RoundedCornerShape(10.dp))
                    .background(ColorsSystem.GradientAvatarPurple),
                contentAlignment = Alignment.Center
            ) {
                Text(character.name.first().uppercaseChar().toString(),
                    fontSize = 13.sp, fontWeight = FontWeight.Black,
                    fontFamily = NunitoFontFamily, color = ColorsSystem.BackgroundCard)
            }
            Column {
                Text(character.name, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold,
                    fontFamily = NunitoFontFamily, color = ColorsSystem.TextPrimary)
                Text("4 QR codes · Format optimisé", fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold, fontFamily = NunitoFontFamily,
                    color = ColorsSystem.TextDisabled)
            }
        }

        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Génération des QR codes…", fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold, fontFamily = NunitoFontFamily,
                    color = ColorsSystem.TextDisabled)
            }
        } else {
            AnimatedContent(targetState = view, label = "view") { v ->
                when (v) {
                    QrView.CAROUSEL -> CarouselView(
                        qrList = qrList,
                        currentIndex = currentIndex,
                        onIndexChange = { currentIndex = it },
                        onQrTap = { zoomedQr = it },
                        character = character,
                        viewModel = viewModel,
                        context = context
                    )
                    QrView.GRID -> GridView(
                        qrList = qrList,
                        onQrTap = { zoomedQr = it },
                        character = character,
                        viewModel = viewModel,
                        context = context
                    )
                }
            }
        }
    }
}

// ─── Vue Carrousel ────────────────────────────────────
@Composable
private fun CarouselView(
    qrList: List<QrMeta>,
    currentIndex: Int,
    onIndexChange: (Int) -> Unit,
    onQrTap: (QrMeta) -> Unit,
    character: Character,
    viewModel: CharacterDetailViewModel,
    context: android.content.Context
) {
    val scope = rememberCoroutineScope()
    val current = qrList.getOrNull(currentIndex) ?: return

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Dots indicateur
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            qrList.forEachIndexed { i, qr ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .height(4.dp)
                        .width(if (i == currentIndex) 24.dp else 12.dp)
                        .clip(RoundedCornerShape(99.dp))
                        .background(if (i == currentIndex) qr.color else ColorsSystem.Divider)
                        .clickable { onIndexChange(i) }
                )
            }
        }

        // Label page
        Text(
            "${currentIndex + 1} · ${current.label.uppercase()}",
            modifier = Modifier.fillMaxWidth(),
            fontSize = 10.sp, fontWeight = FontWeight.ExtraBold,
            fontFamily = NunitoFontFamily, color = current.color,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            letterSpacing = 1.2.sp
        )

        // QR Card — cliquable pour agrandir
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .coloredShadow(ColorsSystem.Shadow.copy(.08f), 18.dp, 16.dp, offsetY = 4.dp)
                .background(ColorsSystem.BackgroundCard, RoundedCornerShape(18.dp))
                .clickable { onQrTap(current) }
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                current.bitmap?.let {
                    Image(bitmap = it.asImageBitmap(), contentDescription = current.label,
                        modifier = Modifier.size(220.dp).clip(RoundedCornerShape(10.dp)))
                }
                // Hint agrandir
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(99.dp))
                        .background(current.colorLight)
                        .padding(horizontal = 12.dp, vertical = 5.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(current.emoji, fontSize = 12.sp)
                    Text("Appuyer pour agrandir", fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = NunitoFontFamily, color = current.color)
                }
                Text("LR${currentIndex + 1} · ${current.label} · ${character.name}",
                    fontSize = 10.sp, fontWeight = FontWeight.SemiBold,
                    fontFamily = NunitoFontFamily, color = ColorsSystem.TextDisabled)
            }
        }

        // Nav Préc / Suivant
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(99.dp))
                    .background(if (currentIndex > 0) ColorsSystem.BackgroundSurface else ColorsSystem.BackgroundSurface.copy(.4f))
                    .border(1.5.dp, ColorsSystem.Divider, RoundedCornerShape(99.dp))
                    .clickable(enabled = currentIndex > 0) { onIndexChange(currentIndex - 1) }
                    .padding(vertical = 11.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("← Préc.", fontSize = 12.5.sp, fontWeight = FontWeight.ExtraBold,
                    fontFamily = NunitoFontFamily,
                    color = if (currentIndex > 0) ColorsSystem.TextSecondary else ColorsSystem.TextDisabled)
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(99.dp))
                    .background(if (currentIndex < qrList.lastIndex) current.color else ColorsSystem.GreenLight)
                    .clickable(enabled = currentIndex < qrList.lastIndex) { onIndexChange(currentIndex + 1) }
                    .padding(vertical = 11.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (currentIndex < qrList.lastIndex) "Suivant →" else "✓ Terminé",
                    fontSize = 12.5.sp, fontWeight = FontWeight.ExtraBold,
                    fontFamily = NunitoFontFamily,
                    color = if (currentIndex < qrList.lastIndex) ColorsSystem.BackgroundCard else ColorsSystem.GreenDark
                )
            }
        }

        // Actions partage
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ShareButton(
                emoji = "💾", label = "Tout télécharger",
                bgColor = ColorsSystem.GreenLight, textColor = ColorsSystem.GreenDark,
                modifier = Modifier.weight(1f)
            ) {
                scope.launch {
                    qrList.forEach { qr ->
                        qr.bitmap?.let {
                            val decorated = decorateBitmap(it, "LogRPG", character.name, qr.emoji, qr.label)
                            viewModel.saveQrToGallery(context, decorated)
                        }
                    }
                    Toast.makeText(context, "✅ 4 QR sauvegardés", Toast.LENGTH_SHORT).show()
                }
            }
            ShareButton(
                emoji = "📤", label = "Partager tout",
                bgColor = ColorsSystem.BlueLight, textColor = ColorsSystem.Blue,
                modifier = Modifier.weight(1f)
            ) {
                scope.launch {
                    val bitmaps = qrList.mapNotNull { qr ->
                        qr.bitmap?.let { decorateBitmap(it, "LogRPG", character.name, qr.emoji, qr.label) }
                    }
                    viewModel.shareQrImages(context, bitmaps)
                }
            }
        }

        Text(
            "Le Joueur B : Importer → Scanner QR",
            modifier = Modifier.fillMaxWidth(),
            fontSize = 10.sp, fontWeight = FontWeight.SemiBold,
            fontFamily = NunitoFontFamily, color = ColorsSystem.TextDisabled,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

// ─── Vue Grille 2x2 ───────────────────────────────────
@Composable
private fun GridView(
    qrList: List<QrMeta>,
    onQrTap: (QrMeta) -> Unit,
    character: Character,
    viewModel: CharacterDetailViewModel,
    context: android.content.Context
) {
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Grille 2x2
        qrList.chunked(2).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { qr ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .coloredShadow(ColorsSystem.Shadow.copy(.08f), 14.dp, 12.dp, offsetY = 3.dp)
                            .background(ColorsSystem.BackgroundCard, RoundedCornerShape(14.dp))
                            .clickable { onQrTap(qr) }
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // QR miniature
                            Box(
                                modifier = Modifier.fillMaxWidth().aspectRatio(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ColorsSystem.BackgroundSurface),
                                contentAlignment = Alignment.Center
                            ) {
                                qr.bitmap?.let {
                                    Image(bitmap = it.asImageBitmap(),
                                        contentDescription = qr.label,
                                        modifier = Modifier.fillMaxSize())
                                }
                                // Overlay hint
                                Box(
                                    modifier = Modifier.fillMaxSize()
                                        .background(qr.colorLight.copy(.4f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🔍", fontSize = 20.sp,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(ColorsSystem.BackgroundCard.copy(.8f))
                                            .padding(4.dp))
                                }
                            }

                            // Label + couleur
                            Text(
                                "${qr.emoji} ${qr.label.uppercase()}",
                                fontSize = 9.5.sp, fontWeight = FontWeight.ExtraBold,
                                fontFamily = NunitoFontFamily, color = qr.color,
                                letterSpacing = .5.sp
                            )
                        }
                    }
                }
                // Remplir si ligne incomplète
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }

        // Actions globales
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(99.dp))
                .background(ColorsSystem.GradientGreen)
                .clickable {
                    scope.launch {
                        qrList.forEach { qr ->
                            qr.bitmap?.let {
                                val decorated = decorateBitmap(it, "LogRPG", character.name, qr.emoji, qr.label)
                                viewModel.saveQrToGallery(context, decorated)
                            }
                        }
                        Toast.makeText(context, "✅ 4 QR sauvegardés", Toast.LENGTH_SHORT).show()
                    }
                }
                .padding(vertical = 13.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("💾 Télécharger les 4 QR", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold,
                fontFamily = NunitoFontFamily, color = ColorsSystem.BackgroundCard)
        }

        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(99.dp))
                .background(ColorsSystem.BlueLight)
                .border(1.5.dp, ColorsSystem.Blue.copy(.3f), RoundedCornerShape(99.dp))
                .clickable {
                    scope.launch {
                        val bitmaps = qrList.mapNotNull { qr ->
                            qr.bitmap?.let { decorateBitmap(it, "LogRPG", character.name, qr.emoji, qr.label) }
                        }
                        viewModel.shareQrImages(context, bitmaps)
                    }
                }
                .padding(vertical = 13.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("📤 Partager les 4 QR", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold,
                fontFamily = NunitoFontFamily, color = ColorsSystem.Blue)
        }

        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(99.dp))
                .background(ColorsSystem.BackgroundSurface)
                .border(1.5.dp, ColorsSystem.Divider, RoundedCornerShape(99.dp))
                .padding(vertical = 13.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Tapez un QR pour l'agrandir et le faire scanner",
                fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                fontFamily = NunitoFontFamily, color = ColorsSystem.TextDisabled)
        }
    }
}

@Composable
private fun ShareButton(
    emoji: String, label: String,
    bgColor: androidx.compose.ui.graphics.Color,
    textColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(emoji, fontSize = 18.sp)
            Text(label, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold,
                fontFamily = NunitoFontFamily, color = textColor,
                modifier = Modifier.padding(top = 2.dp))
        }
    }
}

/**
 * Ajoute une bannière en bas du bitmap QR avec :
 *  - ligne 1 : appName  (ex: "LogRPG")
 *  - ligne 2 : characterName
 *  - ligne 3 : emoji + qrLabel  (ex: "🎲 Stats & Compteurs")
 */
private fun decorateBitmap(
    qr: Bitmap,
    appName: String,
    characterName: String,
    emoji: String,
    qrLabel: String
): Bitmap {
    val bannerHeight = 130
    val padding = 16f
    val out = Bitmap.createBitmap(qr.width, qr.height + bannerHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(out)

    // QR en haut
    canvas.drawBitmap(qr, 0f, 0f, null)

    // Fond bannière — noir profond
    val bgPaint = Paint().apply { color = android.graphics.Color.parseColor("#0D0F1A") }
    canvas.drawRect(0f, qr.height.toFloat(), qr.width.toFloat(), out.height.toFloat(), bgPaint)

    // Ligne séparatrice colorée (violet LogRPG)
    val linePaint = Paint().apply {
        color = android.graphics.Color.parseColor("#7C6AF7")
        strokeWidth = 2f
    }
    canvas.drawLine(padding, qr.height.toFloat() + 2f, qr.width - padding, qr.height.toFloat() + 2f, linePaint)

    // Textes
    val appPaint = Paint().apply {
        color = android.graphics.Color.parseColor("#7C6AF7")
        textSize = 22f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        isAntiAlias = true
    }
    val charPaint = Paint().apply {
        color = android.graphics.Color.parseColor("#E8E6F0")
        textSize = 26f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        isAntiAlias = true
    }
    val typePaint = Paint().apply {
        color = android.graphics.Color.parseColor("#8B899A")
        textSize = 21f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        isAntiAlias = true
    }

    val baseY = qr.height.toFloat() + 14f
    canvas.drawText(appName.uppercase(), padding, baseY + appPaint.textSize, appPaint)
    canvas.drawText(characterName, padding, baseY + appPaint.textSize + 32f, charPaint)
    canvas.drawText("$emoji $qrLabel", padding, baseY + appPaint.textSize + 62f, typePaint)

    return out
}

private enum class QrView { CAROUSEL, GRID }